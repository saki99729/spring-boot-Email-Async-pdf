package com.cheatCode.cashier.service.Qr;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QrService {
    public byte[] generateQrCode(String data) throws Exception {
        try {
            // Primary: use ZXing to generate QR
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, 150, 150);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] qrCodeBytes = pngOutputStream.toByteArray();
            pngOutputStream.close();
            return qrCodeBytes;

        } catch (NoClassDefFoundError ex) {
            // ZXing not available at runtime (devtools classloader or missing dep)
            // Fallback: create a simple placeholder PNG with the text representation
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int size = 150;
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = img.createGraphics();
                try {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, size, size);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                    String[] lines = splitText(data, 4);
                    int y = 20;
                    for (String line : lines) {
                        g2.drawString(line, 10, y);
                        y += 14;
                    }
                } finally {
                    g2.dispose();
                }
                ImageIO.write(img, "PNG", baos);
                return baos.toByteArray();
            }
        }
    }

    // split long text into up to n lines for placeholder
    private static String[] splitText(String text, int maxLines) {
        String[] words = text.split("\\s+");
        String[] lines = new String[maxLines];
        StringBuilder cur = new StringBuilder();
        int lineIdx = 0;
        for (String w : words) {
            if (cur.length() + w.length() + 1 > 20) {
                if (lineIdx < maxLines) lines[lineIdx++] = cur.toString();
                cur.setLength(0);
            }
            if (!cur.isEmpty()) cur.append(' ');
            cur.append(w);
        }
        if (lineIdx < maxLines) lines[lineIdx++] = cur.toString();
        // fill remaining lines with empty
        for (int i = lineIdx; i < maxLines; i++) lines[i] = "";
        return lines;
    }
}
