package com.routingengine;

import static com.routingengine.SupportRequest.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class RequestQueueManager
{
    private Map<Type, RequestQueue> requestQueues;
    
    public RequestQueueManager()
    {
        requestQueues = new HashMap<>();
        
        for (Type requestType : Type.values()) {
            requestQueues.put(requestType, new RequestQueue());
        }
        
        requestQueues = Map.copyOf(requestQueues);
    }
    
    public RequestQueue getQueue(Type requestType)
    {
        return requestQueues.get(requestType);
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
    
        public int getCount()
        {
            return count.intValue();
        }
        
        public boolean contains(SupportRequest supportRequest)
        {
            return queue.contains(supportRequest);
        }
        
        public boolean put(SupportRequest supportRequest)
        {
            if (contains(supportRequest))
                return false;
            
            queue.put(supportRequest);
            
            count.getAndIncrement();
            
            return true;
        }
        
        public boolean remove(SupportRequest supportRequest)
        {
            if (!contains(supportRequest))
                return false;
            
            queue.remove(supportRequest);
            
            count.getAndDecrement();
            
            return true;
        }
        
        public SupportRequest take()
        {
            if (queue.size() == 0)
                return null;
            
            SupportRequest supportRequest = queue.remove();
            
            count.getAndDecrement();
            
            return supportRequest;
        }
        
        public SupportRequest[] toArray()
        {
            return queue.toArray(SupportRequest[]::new);
        }
    }
}
