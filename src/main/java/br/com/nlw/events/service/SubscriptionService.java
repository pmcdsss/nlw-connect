package br.com.nlw.events.service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicatorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.EventRepository;
import br.com.nlw.events.repo.SubscriptionRepository;
import br.com.nlw.events.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
        Event event = eventRepository.findByPrettyName(eventName);
        if (event == null)
            throw new EventNotFoundException("Evento "+ eventName + " não encontrado.");

        User userExist = userRepository.findByEmail(user.getEmail());
        if (userExist == null)
            userExist = userRepository.save(user);

        User indicator = null;

        if (userId != null)
        {
            indicator= userRepository.findById(userId).orElse(null);
            if (indicator == null)
                throw new UserIndicatorNotFoundException("Usuário indicador não existe.");
        }

        Subscription subscription = subscriptionRepository.findByEventAndSubscriber(event, userExist);
        if (subscription != null)
            throw new SubscriptionConflictException("Usuário " + user.getName() + " já inscrito no evento " + event.getPrettyName());

        subscription = new Subscription();
        subscription.setEvent(event);
        subscription.setSubscriber(userExist);
        subscription.setIndication(indicator);

        subscriptionRepository.save(subscription);

        return new SubscriptionResponse(subscription.getSubscriptionNumber(),
                                        "http://codecraft.com/subscription/"+subscription.getEvent().getPrettyName()+"/"+subscription.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
        Event event = eventRepository.findByPrettyName(prettyName);
        if (event == null)
            throw new EventNotFoundException("Evento " + prettyName + " não encontrado.");

        return subscriptionRepository.generateRanking(event.getEventId());
    }

    public SubscriptionRankingByUser getRankingPositionByUser(String prettyName, Integer userId){
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream()
                                       .filter(i -> i.userId().equals(userId))
                                       .findFirst().orElse(null);
        if (item == null)
           throw new UserIndicatorNotFoundException("Não há inscrição com indicação por esse usuário.");

        Integer position = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).userId().equals(userId))
                .findFirst()
                .getAsInt() + 1;

        return new SubscriptionRankingByUser(position, item);
    }
}