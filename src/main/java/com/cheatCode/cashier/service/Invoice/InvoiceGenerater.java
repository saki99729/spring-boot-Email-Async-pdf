package com.cheatCode.cashier.service.Invoice;

import com.cheatCode.cashier.model.db_model.saleModel;
import com.cheatCode.cashier.service.Qr.QrService;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceGenerater {

    private final QrService qrService;

    @Autowired
    public InvoiceGenerater(QrService qrService) {
        this.qrService = qrService;
    }

    public void generateInvoice(String filePath, saleModel sale, String customerEmail) throws Exception {
        File file = new File(filePath);
        // ensure parent directory exists (handle null parent safely)
        if (file.getParentFile() != null) {
            Path parentPath = file.getParentFile().toPath();
            Files.createDirectories(parentPath);
        }

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        // add footer event handler so every page gets the footer
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler());

        Document document = new Document(pdf);

        // Colors
        DeviceRgb companyBlue = new DeviceRgb(0, 102, 204); // main company color
        DeviceRgb tableHeaderColor = new DeviceRgb(0, 123, 255);
        DeviceRgb rowAlt = new DeviceRgb(245, 250, 255);

        // Build a header table: logo on the left, company name centered on the right
        Image logo = null;
        try (InputStream is = InvoiceGenerater.class.getResourceAsStream("/static/logo.png")) {
            if (is != null) {
                byte[] imgBytes = is.readAllBytes();
                ImageDataFactory.create(imgBytes); // ensure import usage
                logo = new Image(ImageDataFactory.create(imgBytes));
                logo.scaleToFit(120, 60);
                logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
            }
        } catch (Exception e) {
            // if logo loading fails, continue without logo
            System.out.println("Logo not added: " + e.getMessage());
        }

        // Header table with two columns: left 20%, right 80%
        Table header = new Table(UnitValue.createPercentArray(new float[] { 20f, 80f }));
        header.setWidth(UnitValue.createPercentValue(100));

        Cell logoCell = new Cell().setBorder(null);
        if (logo != null) {
            logoCell.add(logo);
        }
        header.addCell(logoCell);

        Paragraph companyName = new Paragraph("Nimanka (Pvt) Ltd.")
                .setBold().setFontSize(22)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(companyBlue);
        Cell titleCell = new Cell().add(companyName).setBorder(null)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
        header.addCell(titleCell);

        document.add(header);

        // full width colored underline (use a colored line)
        LineSeparator ls = new LineSeparator(new SolidLine());
        ls.setWidth(UnitValue.createPercentValue(100));
        // color the separator with company color and make it a bit thicker
        ls.setStrokeColor(companyBlue);
       // ls.setLineWidth(2f);
        // add the line separator (no direct renderer call needed)
        document.add(ls);

        // Invoice Info
        String invoiceId = "N/A";
        if (sale != null && sale.getId() != null)
            invoiceId = String.valueOf(sale.getId());
        document.add(new Paragraph("Invoice Date: " + LocalDate.now()));
        document.add(new Paragraph("Customer Email: " + customerEmail));
        document.add(new Paragraph("Invoice ID: " + invoiceId));
        document.add(new Paragraph("\n"));

        // Items Table (single-item saleModel) - full width using percent array
        Table table = new Table(UnitValue.createPercentArray(new float[] { 50f, 15f, 15f, 20f })); // percentages
        table.setWidth(UnitValue.createPercentValue(100));
        // header cells with colored background and white text
        table.addHeaderCell(new Cell().add(new Paragraph("Item Name").setBold().setFontColor(DeviceRgb.WHITE)).setBackgroundColor(tableHeaderColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold().setFontColor(DeviceRgb.WHITE)).setBackgroundColor(tableHeaderColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setBold().setFontColor(DeviceRgb.WHITE)).setBackgroundColor(tableHeaderColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontColor(DeviceRgb.WHITE)).setBackgroundColor(tableHeaderColor));

        double grandTotal = 0;
        // saleModel in this project represents a single sale row (one item)
        if (sale != null) {
            String name = sale.getItemName() != null ? sale.getItemName() : "";
            String quantity = sale.getQuantity() != null ? String.valueOf(sale.getQuantity()) : "0";
            String price = sale.getPrice() != null ? String.valueOf(sale.getPrice()) : "0.0";
            double total = sale.getTotal() != null ? sale.getTotal()
                    : (sale.getQuantity() != null && sale.getPrice() != null ? sale.getQuantity() * sale.getPrice()
                            : 0.0);

            Cell c1 = new Cell().add(new Paragraph(name));
            Cell c2 = new Cell().add(new Paragraph(quantity));
            Cell c3 = new Cell().add(new Paragraph(price));
            Cell c4 = new Cell().add(new Paragraph(String.valueOf(total)));
            // apply a subtle background to the single row
            c1.setBackgroundColor(rowAlt);
            c2.setBackgroundColor(rowAlt);
            c3.setBackgroundColor(rowAlt);
            c4.setBackgroundColor(rowAlt);
            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);

            grandTotal += total;
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        // Total with a colored emphasis bar
        Paragraph totalParagraph = new Paragraph("Grand Total: $" + String.format("%.2f", grandTotal))
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.RIGHT).setFontColor(companyBlue);
        document.add(totalParagraph);
        // Generate and add QR code under the total (uses QrService which has safe fallback)
        try {
            String qrData = "Invoice ID: " + invoiceId + "\nTotal: " + String.format("%.2f", grandTotal);
            byte[] qrImageBytes = qrService.generateQrCode(qrData);
            if (qrImageBytes != null && qrImageBytes.length > 0) {
                Image qrImage = new Image(ImageDataFactory.create(qrImageBytes));
                qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
                qrImage.scaleToFit(120, 120);
                document.add(qrImage);
            }
        } catch (Exception e) {
            System.out.println("QR generation skipped: " + e.getMessage());
        }
        document.add(new Paragraph("\nThank you for your purchase!"));

        document.close();
        System.out.println("Invoice PDF created at: " + filePath);
    }

    // footer event handler that draws a centered footer on every page
    private static class FooterHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();

            float x = pageSize.getLeft();
            float y = pageSize.getBottom();
            float width = pageSize.getWidth();

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
            // draw footer into the content stream we just created using PdfCanvas +
            // Rectangle
            Rectangle footerRect = new Rectangle(x + 36, y + 15, width - 72, 40);
            try (Canvas canvas = new Canvas(pdfCanvas, footerRect)) {
                Paragraph p = new Paragraph(
                        "Nimanka (Pvt) Ltd. | 123 Main St, Colombo | +94 77 123 4567 | info@nimanka.lk")
                        .setFontSize(9)
                        .setTextAlignment(TextAlignment.CENTER);
                canvas.add(p);
            }
            pdfCanvas.release();
        }
    }
}
