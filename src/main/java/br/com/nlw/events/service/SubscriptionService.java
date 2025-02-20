package br.com.nlw.events.service;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.EventRepository;
import br.com.nlw.events.repo.SubscriptionRepository;
import br.com.nlw.events.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public Subscription createNewSubscription(String eventName, User user) {
        Subscription subscription = new Subscription();

        Event event = eventRepository.findByPrettyName(eventName);
        user = userRepository.save(user);

        subscription.setEvent(event);
        subscription.setSubscriber(user);

        return subscriptionRepository.save(subscription);
    }
}
