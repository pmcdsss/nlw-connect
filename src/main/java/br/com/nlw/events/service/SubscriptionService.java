package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
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

    public SubscriptionResponse createNewSubscription(String eventName, User user) {
        Event event = eventRepository.findByPrettyName(eventName);
        if (event == null)
            throw new EventNotFoundException("Evento "+ eventName + " não encontrado.");

        User userExist = userRepository.findByEmail(user.getEmail());
        if (userExist == null)
            userExist = userRepository.save(user);

        Subscription subscription = subscriptionRepository.findByEventAndSubscriber(event, userExist);
        if (subscription != null)
            throw new SubscriptionConflictException("Usuário " + user.getName() + " já inscrito no evento " + event.getPrettyName());

        subscription = new Subscription();
        subscription.setEvent(event);
        subscription.setSubscriber(userExist);
        subscriptionRepository.save(subscription);

        return new SubscriptionResponse(subscription.getSubscriptionNumber(),
                                        "http://codecraft.com/"+subscription.getEvent().getPrettyName()+"/"+subscription.getSubscriber().getId());
    }
}
