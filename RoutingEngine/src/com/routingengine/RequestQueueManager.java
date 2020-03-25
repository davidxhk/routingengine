package com.routingengine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestQueueManager
{
  private Map<SupportRequest.Type, RequestQueue> requestQueues;
  
  public RequestQueueManager()
  {
    requestQueues = new HashMap<>();
    
    for (SupportRequest.Type requestType : SupportRequest.Type.values()) {
      requestQueues.put(requestType, new RequestQueue());
    }
    
    requestQueues = Map.copyOf(requestQueues);
  }
  
  private RequestQueue getQueue(SupportRequest.Type requestType)
  {
    return requestQueues.get(requestType);
  }
  
  private RequestQueue selectQueue(SupportRequest supportRequest)
  {
    return getQueue(supportRequest.getType());
  }
  
  private RequestQueue selectQueue(Agent agent)
  {
    int maxCount = Integer.MIN_VALUE;
    
    RequestQueue queue, maxQueue = null;
    
    for (SupportRequest.Type requestType : agent.getSkills()) {
      queue = getQueue(requestType);
      
      if (maxQueue == null || queue.getCount() > maxCount) {
        maxQueue = queue;
        maxCount = queue.getCount();
      }
    }
    
    return maxQueue;
  }
  
  public void putSupportRequest(SupportRequest supportRequest)
  {
    selectQueue(supportRequest).put(supportRequest);
  }
  
  public void removeSupportRequest(SupportRequest supportRequest)
  {
    selectQueue(supportRequest).remove(supportRequest);
  }
  
  public boolean isQueued(SupportRequest supportRequest)
  {
    return selectQueue(supportRequest).contains(supportRequest);
  }
  
  public SupportRequest takeSupportRequest(Agent agent)
    throws InterruptedException, TimeoutException
  {
    return selectQueue(agent).take();
  }
  
  public int getQueueCount(SupportRequest.Type requestType)
  {
    return getQueue(requestType).getCount();
  }
  
  public SupportRequest[] getQueuedSupportRequests(SupportRequest.Type requestType)
  {
    return getQueue(requestType).queue.toArray(SupportRequest[]::new);
  }
  
   public static class RequestQueue
  {
     private PriorityBlockingQueue<SupportRequest> queue;
    private AtomicInteger count;
    
    private RequestQueue()
    {
      queue = new PriorityBlockingQueue<SupportRequest>();
      count = new AtomicInteger();
    }
  
    private int getCount()
    {
      return count.intValue();
    }
    
    private boolean contains(SupportRequest supportRequest)
    {
      return queue.contains(supportRequest);
    }
    
    private boolean put(SupportRequest supportRequest)
    {
      if (contains(supportRequest))
        return false;
      
      queue.put(supportRequest);
      
      count.getAndIncrement();
      
      return true;
    }
    
    private SupportRequest take()
      throws InterruptedException, TimeoutException
    {
      SupportRequest supportRequest = queue.poll(InetEntity.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
      
      if (supportRequest != null) {
        count.getAndDecrement();
      
        return supportRequest;
      }
      
      else {
        throw new TimeoutException();
      }
    }
    
    private boolean remove(SupportRequest supportRequest)
    {
      boolean removed = queue.remove(supportRequest);
      
      if (removed)
        count.getAndDecrement();
      
      return removed;
    }
  }
}
