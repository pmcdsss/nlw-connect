package br.com.nlw.events.repo;

import br.com.nlw.events.model.Subscription;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> { }
