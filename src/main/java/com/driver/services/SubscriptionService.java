package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    SubscriptionRepository subscriptionRepository;

    UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto)  {

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        int totalAmount = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)) {
            totalAmount = 500 + 200 * subscription.getNoOfScreensSubscribed();
        } else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)) {
            totalAmount = 800 + 250 * subscription.getNoOfScreensSubscribed();
        } else {
            totalAmount = 1000 + 350 * subscription.getNoOfScreensSubscribed();
        }

        subscription.setTotalAmountPaid(totalAmount);
        subscription.setUser(user);
        user.setSubscription(subscription);

        return userRepository.save(user).getSubscription().getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();

        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        if(subscriptionType.equals(SubscriptionType.ELITE)) {
            throw new Exception("Already the best Subscription");
        }

        int noOfScreensSubscribed = 0;
        int totalAmount = subscription.getTotalAmountPaid();
        int newTotalAmount = 0;
        if(subscriptionType.equals(SubscriptionType.BASIC)) {
            noOfScreensSubscribed = (totalAmount - 500) / 200;
            newTotalAmount = 800 + 250 * noOfScreensSubscribed;
            subscription.setSubscriptionType(SubscriptionType.PRO);
        } else {
            noOfScreensSubscribed = (totalAmount - 800) / 250;
            newTotalAmount = 1000 + 350 * noOfScreensSubscribed;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        int diff = newTotalAmount - totalAmount;
        subscription.setTotalAmountPaid(newTotalAmount);
        subscriptionRepository.save(subscription);
        return diff;
    }

    public Integer calculateTotalRevenueOfHotstar() {

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        return subscriptionList.stream()
                .mapToInt(Subscription::getTotalAmountPaid)
                .sum();
    }
}