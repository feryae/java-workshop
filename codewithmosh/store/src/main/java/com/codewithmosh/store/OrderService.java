// Testing dependency injection
package com.codewithmosh.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// OrderService is tightly coupled to StripePaymentService
// So we need to decouple
// public class OrderService {
//     public void placeOrder(){
//         var paymentService = new StripePaymentService();
//         paymentService.processPayment(10);
//     }
// }

// Annotating beans
// @Service and @Component basically has no difference 
// But we use @Service for components that have business logic
@Service
public class OrderService {
    private PaymentService paymentService;
    

    public OrderService(PaymentService paymentService, int x){}

    // From old codebases. It is not necessary if the class has a single constructor    
    // If we have multiple constructor we have to use autowired
    @Autowired 
    public OrderService(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    public void placeOrder(){
        paymentService.processPayment(10);
    }

    // setter injection
    public void setPaymentService(PaymentService paymentService){
        this.paymentService = paymentService;
    }
}

