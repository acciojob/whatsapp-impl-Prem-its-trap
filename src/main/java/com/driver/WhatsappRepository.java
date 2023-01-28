package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String, User> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public boolean userExist(String mobile){
        if(!userMobile.containsKey(mobile)){
            return false;
        }
        return true;
    }
    public String createUser(String name, String mobile){
        User user = new User(name,mobile);
        userMobile.put(mobile,user);
        return "SUCCESS";
    }
    public Group createGroup(List<User> users){
        if(users.size() > 2){
            customGroupCount++;
            Group group = new Group("Group"+Integer.toString(customGroupCount), users.size());
            groupUserMap.put(group, users);
            adminMap.put(group,users.get(0));
            return group;
        }
        Group personalGroup = new Group(users.get(1).getName(), users.size());
        groupUserMap.put(personalGroup,users);
        return personalGroup;
    }
    public int createMessage(String content){
        messageId++;
        Message message = new Message(messageId,content, new Date());
        return messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)) {
            throw new Exception("Group does not exist");
        }
        boolean check=false ;
        for(User user: groupUserMap.get(group)){   // if error then change
            if(user.equals(sender)){
                check=true;
                break;
            }
        }
        if(!check) throw new Exception("You are not allowed to send message");
        List<Message> messages = new ArrayList<>();
        if(groupMessageMap.containsKey(group)){
            messages = groupMessageMap.get(group);
        }
        messages.add(message);
        groupMessageMap.put(group,messages);
        return messages.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        boolean check=false ;
        for(User u: groupUserMap.get(group)){
            if(u.equals(user)){
                check=true;
                break;

            }
        }
        if(!check) throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "SUCCESS";
    }
}
