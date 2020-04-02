package com.routingengine;

import static com.routingengine.Logger.log;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import com.routingengine.RequestQueueManager.RequestQueue;
import com.routingengine.SupportRequest.Type;
import com.routingengine.methods.TakeSupportRequestMethod;

public class RoutingEngineWorker implements Runnable {
  ReentrantLock lock;
  Condition condition;
  RequestQueue requestQueue;
  Map<Type, List<Agent>> availableAgents;
  List<Agent> availableAgentList;
  Type type;
  TakeSupportRequestMethod takeSupportRequestMethod;

  RoutingEngineWorker(ReentrantLock lock, Condition condition, RequestQueue requestQueue,
      Map<Type, List<Agent>> availableAgents, Type type, RoutingEngine routingEngine) {
    this.lock = lock;
    this.requestQueue = requestQueue;
    this.availableAgents = availableAgents;
    this.type = type;
    this.condition = condition;
    this.availableAgentList = availableAgents.get(type);
    takeSupportRequestMethod = new TakeSupportRequestMethod();
    takeSupportRequestMethod.routingEngine = routingEngine;
  }

  @Override
  public void run() {
    log("Worker is running");
    while (true) {
      try {
        lock.lock();
        while (requestQueue.getCount() == 0 || availableAgentList.size() == 0) {
          log(String.format(
              "%s Request queue currently has %d support request(s) in queue and %d available agents",
              type.toString(), requestQueue.getCount(), availableAgentList.size()));
          condition.await();
        }
        clearQueue();
      } catch (InterruptedException e) {
        log("Thread Interrupted");
      } finally {
        lock.unlock();
      }
    }
  }

  private void clearQueue() {
    log(String.format("%s Worker of type now clearing queue", type.toString()));
    Agent agent = availableAgentList.remove(0);
    removeAgentFromMap(agent);
    takeSupportRequestMethod.takeSupportRequest(agent);
  }

  private void removeAgentFromMap(Agent agent) {
    if (agent.getSkills().length > 1) {
      for (Type type : agent.getSkills()) {
        if (type != this.type) {
          availableAgents.get(type).remove(agent);
        }
      }
    }
  }

}
