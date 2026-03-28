package MedicalEcommerce.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class UpiPaymentService {

    @Value("${upi.merchant.id}")
    private String merchantUpiId;

    @Value("${upi.merchant.name}")
    private String merchantName;

    /**
     * Generates a UPI deep-link URL compatible with GPay, PhonePe, Paytm, etc.
     */
    public String generateUpiUrl(double amount, String description) {
        String encodedDesc = description.replace(" ", "%20");
        String encodedName = merchantName.replace(" ", "%20");
        return String.format(
            "upi://pay?pa=%s&pn=%s&am=%.2f&cu=INR&tn=%s",
            merchantUpiId, encodedName, amount, encodedDesc
        );
    }

    /**
     * Generates a Base64-encoded PNG QR code image for the given UPI URL.
     */
    public String generateQrCodeBase64(String upiUrl) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(upiUrl, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
