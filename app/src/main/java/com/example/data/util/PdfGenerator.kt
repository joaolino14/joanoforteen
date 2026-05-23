package com.example.data.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.data.model.Resume
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    fun generateResumePdf(context: Context, resume: Resume): File? {
        val pdfDocument = PdfDocument()
        
        // Setup standard 72 DPI A4 dimensions: 595 x 842 points
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        
        val paint = Paint().apply {
            isAntiAlias = true
        }
        
        var y = 45f
        val margin = 45f
        val width = 595f - (margin * 2)
        
        // Background coloring for dark mode template
        val isDark = resume.templateId == "dark"
        if (isDark) {
            paint.color = Color.parseColor("#0F172A") // Zinc 900
            canvas.drawRect(0f, 0f, 595f, 842f, paint)
        }
        
        // Title banner header background if "corporate" template
        if (resume.templateId == "corporate") {
            paint.color = Color.parseColor("#1E293B") // Slate 800
            canvas.drawRect(0f, 0f, 595f, 135f, paint)
            y = 150f
        }
        
        // Define text colors
        val textColorPrimary = if (isDark) "#F8FAFC" else "#1E293B"
        val textColorSecondary = if (isDark) "#94A3B8" else "#64748B"
        
        val accentColor = when (resume.templateId) {
            "creative" -> "#EC4899" // Pink accent
            "corporate" -> "#3B82F6" // Standard blue accent
            "minimalist" -> "#475569" // Muted slate accent
            else -> "#0F172A" // Contrast theme
        }
        
        // 1. Full Name
        paint.color = if (resume.templateId == "corporate") Color.WHITE else Color.parseColor(textColorPrimary)
        paint.textSize = 22f
        paint.isFakeBoldText = true
        val startTitleY = if (resume.templateId == "corporate") 65f else y
        canvas.drawText(resume.fullName.ifEmpty { "YOUR FULL NAME" }, margin, startTitleY, paint)
        
        // Role Title
        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = if (resume.templateId == "corporate") Color.parseColor("#94A3B8") else Color.parseColor(accentColor)
        canvas.drawText(resume.targetRole.ifEmpty { "Target professional role/title" }.uppercase(), margin, startTitleY + 22f, paint)
        
        // Contact details row
        paint.textSize = 9f
        paint.color = if (resume.templateId == "corporate") Color.WHITE else Color.parseColor(textColorSecondary)
        val contactLine = "${resume.email}   |   ${resume.phone}   |   ${resume.location}"
        canvas.drawText(contactLine, margin, startTitleY + 41f, paint)
        
        // Optional Web row
        if (resume.website.isNotEmpty() || resume.linkedin.isNotEmpty() || resume.github.isNotEmpty()) {
            val netList = listOfNotNull(
                resume.website.takeIf { it.isNotEmpty() },
                resume.linkedin.takeIf { it.isNotEmpty() },
                resume.github.takeIf { it.isNotEmpty() }
            ).joinToString("   |   ")
            canvas.drawText(netList, margin, startTitleY + 54f, paint)
        }
        
        if (resume.templateId != "corporate") {
            y += 75f
        }
        
        // Section separator line
        paint.color = Color.parseColor(if (isDark) "#334155" else "#E2E8F0")
        canvas.drawLine(margin, y - 5f, 595f - margin, y - 5f, paint)
        y += 15f
        
        // Helper block drawer
        fun drawSectionHeader(title: String) {
            paint.color = Color.parseColor(accentColor)
            paint.textSize = 11f
            paint.isFakeBoldText = true
            canvas.drawText(title, margin, y, paint)
            y += 7f
            paint.color = Color.parseColor(if (isDark) "#1E293B" else "#F1F5F9")
            canvas.drawRect(margin, y, 595f - margin, y + 1f, paint)
            y += 14f
        }
        
        // 2. Summary
        if (resume.summary.isNotEmpty()) {
            drawSectionHeader("PROFESSIONAL SUMMARY")
            paint.color = Color.parseColor(textColorPrimary)
            paint.textSize = 9.5f
            paint.isFakeBoldText = false
            
            val lines = wrapText(resume.summary, paint, width)
            for (line in lines) {
                canvas.drawText(line, margin, y, paint)
                y += 14f
            }
            y += 15f
        }
        
        // 3. Work History
        val workList = JsonUtils.jsonToWorkList(resume.workHistoryJson)
        if (workList.isNotEmpty()) {
            drawSectionHeader("WORK EXPERIENCE")
            for (work in workList) {
                paint.color = Color.parseColor(textColorPrimary)
                paint.textSize = 10f
                paint.isFakeBoldText = true
                canvas.drawText("${work.jobTitle} at ${work.company}", margin, y, paint)
                
                paint.textSize = 9f
                paint.isFakeBoldText = false
                paint.color = Color.parseColor(textColorSecondary)
                val durationText = "${work.duration} (${work.location})"
                canvas.drawText(durationText, 595f - margin - paint.measureText(durationText), y, paint)
                y += 14f
                
                // Bullet points
                paint.color = Color.parseColor(textColorPrimary)
                val paragraphs = work.description.split("\n")
                for (p in paragraphs) {
                    if (p.trim().isEmpty()) continue
                    val bulletText = if (p.trim().startsWith("•")) p.trim() else "• ${p.trim()}"
                    val wrappedLines = wrapText(bulletText, paint, width - 10f)
                    for (line in wrappedLines) {
                        canvas.drawText(line, margin + 8f, y, paint)
                        y += 13f
                    }
                }
                y += 10f
            }
            y += 5f
        }
        
        // 4. Skills
        val skills = JsonUtils.jsonToSkills(resume.skillsJson)
        if (skills.isNotEmpty()) {
            drawSectionHeader("CORE SKILLS")
            paint.color = Color.parseColor(textColorPrimary)
            paint.textSize = 9.5f
            paint.isFakeBoldText = false
            
            val skillLine = skills.joinToString("   •   ")
            val wrappedSkills = wrapText(skillLine, paint, width)
            for (line in wrappedSkills) {
                canvas.drawText(line, margin, y, paint)
                y += 14f
            }
            y += 15f
        }
        
        // 5. Education
        val eduList = JsonUtils.jsonToEducationList(resume.educationJson)
        if (eduList.isNotEmpty()) {
            drawSectionHeader("EDUCATION")
            for (edu in eduList) {
                paint.color = Color.parseColor(textColorPrimary)
                paint.textSize = 10f
                paint.isFakeBoldText = true
                canvas.drawText("${edu.degree} — ${edu.school}", margin, y, paint)
                
                paint.textSize = 9f
                paint.isFakeBoldText = false
                paint.color = Color.parseColor(textColorSecondary)
                canvas.drawText(edu.duration, 595f - margin - paint.measureText(edu.duration), y, paint)
                
                if (edu.grade.isNotEmpty()) {
                    y += 13f
                    paint.color = Color.parseColor(textColorPrimary)
                    canvas.drawText("Performance/GPA: ${edu.grade}", margin, y, paint)
                }
                y += 18f
            }
            y += 5f
        }
        
        // 6. Projects
        val projectsList = JsonUtils.jsonToProjectList(resume.projectsJson)
        if (projectsList.isNotEmpty()) {
            drawSectionHeader("PROJECTS & RELEASES")
            for (proj in projectsList) {
                paint.color = Color.parseColor(textColorPrimary)
                paint.textSize = 10f
                paint.isFakeBoldText = true
                canvas.drawText("${proj.title} [Key Contributor]", margin, y, paint)
                
                paint.textSize = 9f
                paint.isFakeBoldText = false
                paint.color = Color.parseColor(textColorSecondary)
                canvas.drawText(proj.duration, 595f - margin - paint.measureText(proj.duration), y, paint)
                y += 14f
                
                paint.color = Color.parseColor(textColorPrimary)
                val lines = wrapText(proj.description, paint, width - 10f)
                for (line in lines) {
                    canvas.drawText(line, margin + 8f, y, paint)
                    y += 13f
                }
                y += 10f
            }
        }
        
        pdfDocument.finishPage(page)
        
        val sanitizedTitle = resume.title.ifEmpty { "Draft" }.replace("\\s+".toRegex(), "_")
        val file = File(context.cacheDir, "${sanitizedTitle}_CV.pdf")
        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
    
    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split("\\s+".toRegex())
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth) {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                currentLine = StringBuilder(testLine)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }
}
