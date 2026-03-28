package MedicalEcommerce.controller;

import MedicalEcommerce.model.PaymentDtls;
import MedicalEcommerce.repository.PaymentRepository;
import MedicalEcommerce.service.UpiPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UpiPaymentController {

    @Autowired
    UpiPaymentService upiPaymentService;

    @Autowired
    PaymentRepository paymentRepository;

    /**
     * Handles the payment form POST: generates UPI URL + QR code,
     * then renders the UPI payment page.
     */
    @PostMapping("/pay")
    public String showUpiPayment(@ModelAttribute PaymentDtls paymentDtls, Model model) {
        try {
            String upiUrl = upiPaymentService.generateUpiUrl(
                    paymentDtls.getPrice(), paymentDtls.getDescription());
            String qrBase64 = upiPaymentService.generateQrCodeBase64(upiUrl);

            model.addAttribute("qrCode", qrBase64);
            model.addAttribute("upiUrl", upiUrl);
            model.addAttribute("amount", paymentDtls.getPrice());
            model.addAttribute("description", paymentDtls.getDescription());
            return "upi-payment";

        } catch (Exception e) {
            e.printStackTrace();
            return "Payment-failed";
        }
    }

    /**
     * Called when the user enters their UTR/transaction reference
     * after completing payment in their UPI app.
     */
    @PostMapping("/pay/confirm")
    public String confirmPayment(@RequestParam String utrNumber,
                                 @RequestParam double amount,
                                 @RequestParam String description) {
        PaymentDtls paymentDtls = new PaymentDtls();
        paymentDtls.setPayment_id(utrNumber);
        paymentDtls.setMethod("UPI");
        paymentDtls.setIntent("sale");
        paymentDtls.setDescription(description);
        paymentDtls.setPrice(amount);
        paymentDtls.setCurrency("INR");
        paymentRepository.save(paymentDtls);
        return "redirect:/paymentSuccess";
    }

    /**
     * Handles payment cancellation.
     */
    @GetMapping("/pay/cancel")
    public String cancelPay() {
        return "Payment-failed";
    }
}
