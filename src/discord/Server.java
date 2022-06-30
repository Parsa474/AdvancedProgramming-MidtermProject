package discord;

import signals.*;

import java.io.IOException;
import java.util.*;

public class Server implements Asset {
    // Fields:
    private final int unicode;
    private String serverName;
    private String owner;
    private HashMap<String, Role> serverRoles;      // maps the roles' names to their Role object
    private HashMap<String, HashSet<Role>> members;     // maps the members' username to their set of roles
    private ArrayList<TextChannel> textChannels;

    // Constructors:
    public Server(int unicode, String serverName, String creator) {
        // construct and initialize the fields
        this.unicode = unicode;
        this.serverName = serverName;
        owner = creator;
        serverRoles = new HashMap<>();
        members = new HashMap<>();
        textChannels = new ArrayList<>();

        //a "member" role with just the SeeChatHistory Ability is added to the roles of the server
        Role memberRole = new Role("member", new HashSet<>(List.of(Ability.SeeChatHistory)));
        serverRoles.put(memberRole.getRoleName(), memberRole);

        //give the owner an "ownerRole" (containing all the abilities), as well as the member role
        HashSet<Ability> ownerAbilities = new HashSet<>(Arrays.asList(Ability.values()));
        Role ownerRole = new Role("owner", ownerAbilities);
        HashSet<Role> ownerRoleSet = new HashSet<>(List.of(ownerRole, memberRole));
        members.put(creator, ownerRoleSet);

        //initialize the members of the general char with just the creator
        ArrayList<String> generalMembers = new ArrayList<>(Collections.singletonList(creator));

        //initialize the first default text channel called general
        textChannels.add(new TextChannel("general", generalMembers, new ArrayList<>()));
    }

    // Getters:
    public int getUnicode() {
        return unicode;
    }

    public String getServerName() {
        return serverName;
    }

    public String getOwner() {
        return owner;
    }

    public HashMap<String, Role> getServerRoles() {
        return serverRoles;
    }

    public HashMap<String, HashSet<Role>> getMembers() {
        return members;
    }

    public ArrayList<TextChannel> getTextChannels() {
        return textChannels;
    }

    // Other Methods:
    public void addNewMember(String username) {
        members.put(username, new HashSet<>(List.of(serverRoles.get("member"))));  // anyone gets the "member" role
        textChannels.get(0).getMembers().put(username, false);     //anyone gets added to the general text channel
    }

    public void enter(ClientController clientController) throws IOException, ClassNotFoundException {
        outer:
        while (true) {

            clientController.getPrinter().printServerMenu();
            int command = clientController.getMyScanner().getInt(1, 6);

            //update server from MainServer
            Server updatedThis = clientController.getMySocket().sendSignalAndGetResponse(new GetServerFromMainServerAction(unicode));
            if (updatedThis == null) {
                clientController.getPrinter().printErrorMessage("db");
                return;
            }
            updateThisFromMainServer(updatedThis);

            // get all the member's abilities
            HashSet<Ability> abilities = new HashSet<>();
            for (Role role : members.get(clientController.getUser().getUsername())) {
                abilities.addAll(role.getAbilities());
            }

            switch (command) {
                case 1 -> changeInfo(clientController, abilities);
                case 2 -> addOrRemoveMembers(clientController, abilities);  //to do: send signals for the added friends
                case 3 -> addOrRemoveTextChannels(clientController, abilities);
                case 4 -> enterATextChannel(clientController, abilities);
                case 5 -> seeAllMembersRoles();
                case 6 -> {
                    break outer;
                }
            }
            //updateThisOnMainServer(clientController);
        }
    }

    public void updateThisOnMainServer(ClientController clientController) throws IOException, ClassNotFoundException {
        boolean DBConnect = clientController.getMySocket().sendSignalAndGetResponse(new UpdateServerOnMainServerAction(this));
        if (!DBConnect) {
            clientController.getPrinter().printErrorMessage("db");
        }
    }

    private void updateThisFromMainServer(Server updatedThis) {
        serverName = updatedThis.serverName;
        owner = updatedThis.owner;
        serverRoles = updatedThis.serverRoles;
        members = updatedThis.members;
        textChannels = updatedThis.textChannels;
    }

    private void seeAllMembersRoles() {
        for (String username : members.keySet()) {
            HashSet<Role> roles = members.get(username);
            System.out.println(username);
            for (Role role : roles) {
                System.out.println(role.toString());
            }
        }
    }

    private void changeInfo(ClientController clientController, HashSet<Ability> abilities) throws IOException, ClassNotFoundException {
        outer:
        while (true) {
            clientController.getPrinter().printServerChangeInfoMenu();
            int command = clientController.getMyScanner().getInt(1, 4);
            switch (command) {
                case 1 -> {
                    if (abilities.contains(Ability.ChangeServerName)) {
                        clientController.getPrinter().printGetMessage("new server name");
                        clientController.getPrinter().printGoBackMessage();
                        serverName = clientController.getMyScanner().getLine();
                        if ("".equals(serverName)) {
                            break;
                        }
                        clientController.getPrinter().printSuccessMessage("server name change");
                    } else {
                        clientController.getPrinter().printErrorMessage("server name change");
                    }
                }
                case 2 -> {
                    if (abilities.contains(Ability.Owner)) {
                        clientController.getPrinter().printTextChannelList(textChannels);
                        clientController.getPrinter().println("enter 0 to go back");
                        int index = clientController.getMyScanner().getInt(0, textChannels.size());
                        if (index == 0) {
                            break;
                        }
                        clientController.getPrinter().printGetMessage("new text channel name");
                        String newTextChannelName = clientController.getMyScanner().getLine();
                        textChannels.get(index - 1).setName(newTextChannelName);
                    } else {
                        clientController.getPrinter().printErrorMessage("only the owner can make this change!");
                    }
                }
                case 3 -> {
                    if (abilities.contains(Ability.Owner)) {
                        createOrEditARole(clientController);
                    } else {
                        clientController.getPrinter().printErrorMessage("Only the owner can access this part!");
                    }
                }
                case 4 -> {
                    break outer;
                }
            }
            updateThisOnMainServer(clientController);
        }
    }

    private void createOrEditARole(ClientController clientController) {
        clientController.getPrinter().printRoleEditMenu();
        switch (clientController.getMyScanner().getInt(1, 3)) {
            case 1 -> {
                Role newRole = createNewRole(clientController);
                clientController.getPrinter().println("Enter the usernames of the members you want to give this role to");
                clientController.getPrinter().println("the usernames must be seperated by a space (invalid usernames will be ignored)");
                clientController.getPrinter().printHashMapList(members.keySet());
                clientController.getPrinter().printGoBackMessage();
                String list = clientController.getMyScanner().getLine();
                if ("".equals(list)) {
                    break;
                }
                addInitialRoleHolders(newRole, list);
            }
            case 2 -> editARole(clientController);
        }
    }

    private Role createNewRole(ClientController clientController) {
        clientController.getPrinter().println("What is the name of the new role?");
        String newRoleName;
        do {
            newRoleName = clientController.getMyScanner().getLine();
        } while ("".equals(newRoleName.trim()));
        clientController.getPrinter().println("What abilities does this role have?");
        clientController.getPrinter().printAbilityList();
        clientController.getPrinter().println("(enter the numbers seperated by a space)");
        ArrayList<Integer> abilityIndexes = clientController.getIntList(8); // indexes received range: 0-7
        Role newRole = new Role(newRoleName, new HashSet<>());
        for (int abilityIndex : abilityIndexes) {
            newRole.getAbilities().add(Ability.values()[abilityIndex + 1]); // ability 0 is only for the owner
        }
        serverRoles.put(newRole.getRoleName(), newRole);
        clientController.getPrinter().printSuccessMessage("new role");
        return newRole;
    }

    private void addInitialRoleHolders(Role newRole, String list) {
        String[] initialRoleHolders = list.split(" ");
        for (String member : initialRoleHolders) {
            member = member.trim();
            if (members.containsKey(member)) {
                members.get(member).add(newRole);
            }
        }
    }

    private void editARole(ClientController clientController) {
        clientController.getPrinter().printGetMessage("enter the name of the role you want to edit");
        clientController.getPrinter().printHashMapList(serverRoles.keySet());
        clientController.getPrinter().printGoBackMessage();
        String roleName;
        while (true) {
            roleName = clientController.getMyScanner().getLine();
            if ("".equals(roleName)) {
                return;
            }
            if (serverRoles.containsKey(roleName)) {
                break;
            } else {
                clientController.getPrinter().printErrorMessage("Invalid name!");
            }
        }
        clientController.getPrinter().println("What abilities will this role have after the change?");
        clientController.getPrinter().printAbilityList();
        clientController.getPrinter().println("(enter the numbers seperated by a space)");
        ArrayList<Integer> abilityIndexes = clientController.getIntList(8); // indexes received range: 0-7
        Role roleUnderEdit = serverRoles.get(roleName);
        roleUnderEdit.getAbilities().clear();
        for (int abilityIndex : abilityIndexes) {
            roleUnderEdit.getAbilities().add(Ability.values()[abilityIndex + 1]); // ability 0 is only for the owner
        }
        clientController.getPrinter().printSuccessMessage("edit role");
    }

    private void addOrRemoveMembers(ClientController clientController, HashSet<Ability> abilities) throws IOException, ClassNotFoundException {
        clientController.getPrinter().printMemberEditMenu();
        int command = clientController.getMyScanner().getInt(1, 3);
        switch (command) {
            case 1 -> {
                if (clientController.addFriendsToServer(this)) {
                    clientController.getPrinter().printSuccessMessage("friend add");
                }
            }
            case 2 -> {
                if (abilities.contains(Ability.RemoveMember)) {
                    removeMembersFromServer();
                } else {
                    clientController.getPrinter().printErrorMessage("permission");
                }
            }
        }
        updateThisOnMainServer(clientController);
    }

    private void removeMembersFromServer() {

    }

    private void addOrRemoveTextChannels(ClientController clientController, HashSet<Ability> abilities) {

    }

    private void enterATextChannel(ClientController clientController, HashSet<Ability> abilities) throws IOException, ClassNotFoundException {

        Model user = clientController.getUser();
        View printer = clientController.getPrinter();
        MyScanner myScanner = clientController.getMyScanner();
        MySocket mySocket = clientController.getMySocket();

        printer.printTextChannelList(textChannels);
        int index = clientController.getMyScanner().getInt(1, textChannels.size()) - 1;
        TextChannel selectedTextChannel = textChannels.get(index);

        selectedTextChannel.getMembers().replace(user.getUsername(), true);
        updateThisOnMainServer(clientController);

        ArrayList<String> receivers = new ArrayList<>(selectedTextChannel.getMembers().keySet());
        receivers.remove(user.getUsername());   // remove oneself from the receivers

        // printing previous messages for the people who have the access to see chat history
        if (abilities.contains(Ability.SeeChatHistory)) {
            printer.printList(selectedTextChannel.getMessages());
        }

        // receiving messages
        Thread listener = new Thread(new TextChannelListener(clientController));
        listener.start();

        // sending message
        printer.println("enter \"#exit\" to exit the chat");
        while (true) {
            String message = myScanner.getLine();
            try {
                mySocket.write(new TextChannelChatAction(user.getUsername(), message, unicode, index, receivers));
                if (message.equals("#exit")) {
                    break;
                }
            } catch (IOException e) {
                printer.printErrorMessage("IO");
            }
        }

        synchronized (user.getUsername()) {
            try {
                user.getUsername().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            selectedTextChannel = mySocket.sendSignalAndGetResponse(new UpdateTextChannelOfServerFromMainServer(unicode, index));
            selectedTextChannel.getMembers().replace(user.getUsername(), false);
        }
        boolean DBConnect = mySocket.sendSignalAndGetResponse(new UpdateTextChannelOfServerOnMainServer(unicode, index, selectedTextChannel));
        if (!DBConnect) {
            printer.printErrorMessage("db");
        }
    }
}
