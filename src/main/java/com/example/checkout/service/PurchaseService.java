package com.example.checkout.service;

import com.example.checkout.api.PurchaseRequest;
import com.example.checkout.api.PurchaseResponse;
import com.example.checkout.domain.Order;
import com.example.checkout.domain.User;
import com.example.checkout.payment.PaymentStrategy;
import com.example.checkout.repository.ItemRepository;
import com.example.checkout.repository.OrderRepository;
import com.example.checkout.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PurchaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Map<String, PaymentStrategy> paymentStrategies;

    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);

    @Transactional
    public PurchaseResponse purchase(PurchaseRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();

        double total = 0;
        for (PurchaseRequest.ItemLine line : request.getItems()) {
            total += line.getUnitPrice() * line.getQuantity();
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setTotalAmount(total);
        orderRepository.save(order);

        PaymentStrategy strategy = paymentStrategies.get(request.getPaymentMethod());

        log.info("Charging user {} phone {} amount {}",
                user.getId(), user.getPhoneNumber(), total);

        boolean paid = strategy.pay(user.getPhoneNumber(), total);

        order.setPaid(paid);
        orderRepository.save(order);

        return new PurchaseResponse(paid, total, paid ? "OK" : "Payment failed");
    }
}
