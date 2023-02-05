package com.example.ProducerConsumerBack;

import com.google.gson.Gson;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Vector;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/get")public class Main {
//    Gson gson = new Gson();
    Vector<Q> queue = new Vector<>();
    Vector<Machine> machine = new Vector<>();
    Q q;
    Machine m;
    String res = "";
    Vector<Thread> threads = new Vector<>();
    Vector<Integer> times = new Vector<>();
    Vector<Product> prods = new Vector<>();
    SimpMessagingTemplate messagingTemplate;
    public Main (SimpMessagingTemplate messagingTemplate) {
        this.m = new Machine(messagingTemplate);
        this.q = new Q(messagingTemplate);
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/expression")
    public String main(@RequestBody String expression) throws Exception {
        System.out.println(expression);
        String[] s = expression.split(",");
        switch (s[0]){
            case "addQueue" :
                q = new Q(messagingTemplate);
                q.setName(Integer.toString(queue.size()));
                queue.add(q);
                break;
            case "addMachine" :
                m = new Machine(messagingTemplate);
                m.setName(Integer.toString(machine.size()));
                machine.add(m);
                times.add(machine.elementAt(machine.size()-1).getWorkingTime());
                break;
            case "link" :
                if(s[1].charAt(1) == 'Q'){
                    q = queue.elementAt(Integer.parseInt(String.valueOf(s[1].charAt(2))));
                    m = machine.elementAt(Integer.parseInt(String.valueOf(s[2].charAt(2))));
                    q.addMachine(m);
                }
                else {
                    m = machine.elementAt(Integer.parseInt(String.valueOf(s[1].charAt(2))));
                    q = queue.elementAt(Integer.parseInt(String.valueOf(s[2].charAt(2))));
                    m.setNextQ(q);
                }
                break;
            case "addProduct" :
                q = queue.elementAt(0);
                Product product = new Product(s[1],s[2]);
                q.addProduct(product);
                prods.add(product);
                break;
            case "run" :
                Thread.sleep(2000);
                for(int i=0; i< queue.size(); i++){
                    Thread myThread = new Thread(queue.elementAt(i));
                    threads.add(myThread);
                    myThread.start();

                }
                for(int i=0; i< machine.size(); i++){
                    Thread myThread = new Thread(machine.elementAt(i));
                    threads.add(myThread);
                    myThread.start();
                }
                break;
            case "getProducts" :
                q = queue.elementAt(Integer.parseInt(s[1]));
                res = q.getProducts();
                System.out.println(res);
                break;
            case "reset":
                for(int i=0; i< threads.size(); i++){
                    threads.elementAt(i).stop();
                }
                threads.clear();
                machine.clear();
                queue.clear();
                prods.clear();
                break;
            case "replay":
                Thread.sleep(2000);
                for(int i=0; i< threads.size(); i++){
                    threads.elementAt(i).stop();
                }
                threads.clear();
                queue.elementAt(queue.size()-1).clear();
                for(int i=0; i< prods.size(); i++){
                    q = queue.elementAt(0);
                    q.addProduct(prods.elementAt(i));
                }
                for(int i=0; i< queue.size(); i++){
                    Thread myThread = new Thread(queue.elementAt(i));
                    threads.add(myThread);
                    myThread.start();

                }
                for(int i=0; i< machine.size(); i++){
                    Thread myThread = new Thread(machine.elementAt(i));
                    threads.add(myThread);
                    myThread.start();
                }
                break;
            /////// socket
        }
//        for(int i=0;i<queue.size();i++) System.out.println(queue.elementAt(i));
//        for(int i=0;i<machine.size();i++) System.out.println(machine.elementAt(i));
        return res;
    }
}