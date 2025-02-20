package br.com.nlw.events.repo;

import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    public Subscription findByEventAndSubscriber(Event event, User user);

    @Query(value = "SELECT SUB.INDICATION_USER_ID, USR.USER_NAME, COUNT(1) as QTD       " +
                   "  FROM TBL_SUBSCRIPTION SUB                                         " +
                   "  JOIN TBL_USER         USR ON USR.USER_ID = SUB.INDICATION_USER_ID " +
                   "  JOIN TBL_EVENT        EVT ON EVT.EVENT_ID = SUB.EVENT_ID          " +
                   " WHERE EVT.EVENT_ID = :event_id                                     " +
                   " GROUP BY SUB.INDICATION_USER_ID, USR.USER_NAME                     " +
                   " ORDER BY QTD DESC", nativeQuery = true)
    public List<SubscriptionRankingItem> generateRanking(@Param("event_id") Integer eventId);
}
