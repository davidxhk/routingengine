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
  Type type;
  TakeSupportRequestMethod takeSupportRequestMethod;

  /**
   * Worker thread specifically to look at the support request queue of a chosen type. It will
   * act on behalf of the agents to take the support request.
   *
   * @param lock            Lock object must be the same for other RoutingEngineWorker. Lock must
   *                        also be held by the routingEngine
   * @param condition       Condition to signal when there is a new support request or when there is
   *                        a newly available agent
   * @param requestQueue    The request queue object the thread is tasked to monitor
   * @param availableAgents Map of available agents - shared resource
   * @param type            The particular type of request queue the thread is tasked to monitor
   * @param routingEngine
   */
  RoutingEngineWorker(ReentrantLock lock, Condition condition, RequestQueue requestQueue,
      Map<Type, List<Agent>> availableAgents, Type type, RoutingEngine routingEngine) {
    this.lock = lock;
    this.requestQueue = requestQueue;
    this.availableAgents = availableAgents;
    this.type = type;
    this.condition = condition;
    takeSupportRequestMethod = new TakeSupportRequestMethod();
    takeSupportRequestMethod.routingEngine = routingEngine;
  }

  /**
   * Main loop
   */
  @Override
  public void run() {
    log("Worker is running");
    while (true) {
      try {
        lock.lock();
        while (requestQueue.getCount() == 0 || availableAgents.get(type).size() == 0) {
          log(String.format(
              "%s Request Queue currently has %d support request(s) in queue and %d available agents",
              type.toString(), requestQueue.getCount(), availableAgents.get(type).size()));
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


  /**
   * Pops the first element off the list of available agents and invokes the takeSupportRequest
   * method with the agent that is popped as the argument.
   */
  private void clearQueue() {
    log(String.format("%s Queue Worker now clearing queue", type.toString()));
    Agent agent = availableAgents.get(type).remove(0);
    removeAgentFromMap(agent);
    takeSupportRequestMethod.takeSupportRequest(agent);
  }


  /**
   * Removes the specified agent from the Map containing listing all currently available agents
   *
   * @param agent The agent to be removed from the available list
   */
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
