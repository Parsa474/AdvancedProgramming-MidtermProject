package discord;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Server implements Serializable {

    private final int unicode;
    private String serverName;
    private final String owner;
    private final HashMap<String, Role> serverRoles;
    private final HashMap<String, HashSet<Role>> members;
    private final ArrayList<TextChannel> textChannels;


    public Server(int unicode, String serverName, String creator) {

        this.unicode = unicode;
        this.serverName = serverName;
        owner = creator;
        serverRoles = new HashMap<>();
        members = new HashMap<>();
        textChannels = new ArrayList<>();

        //a member role without any ability is added to the roles of the server
        Role memberRole = new Role("member", new HashSet<>());
        serverRoles.put(memberRole.getRoleName(), memberRole);

        //give the owner an "ownerRole" (containing all the abilities), as well as the member role
        HashSet<Ability> ownerAbilities = new HashSet<>(Arrays.asList(Ability.values()));
        Role ownerRole = new Role("owner", ownerAbilities);
        HashSet<Role> ownerRoleSet = new HashSet<>(List.of(ownerRole, memberRole));
        members.put(creator, ownerRoleSet);

        //initialize the members of the general char with just the creator
        ArrayList<String> generalMembers = new ArrayList<>();
        generalMembers.add(creator);

        //initialize the first default text channel called general
        textChannels.add(new TextChannel(unicode, 0, "general", generalMembers, new ArrayList<>()));
    }

    public void addNewMember(String username) {
        members.put(username, new HashSet<>());                    //new HashSet of roles they may have
        members.get(username).add(serverRoles.get("member"));      //anyone gets the "member" role at first
        textChannels.get(0).getMembers().put(username, false);     //anyone gets added to the general text channel
    }

    public int getUnicode() {
        return unicode;
    }

    public String getServerName() {
        return serverName;
    }

    public HashMap<String, HashSet<Role>> getMembers() {
        return members;
    }

    public enum Ability {
        CreateChannel,
        RemoveChannel,
        RemoveMember,
        LimitMembersOfChannels,
        Ban,
        ChangeServerName,
        SeeChatHistory,
        PinMessage
    }

    public void enter(ClientController clientController) throws IOException {
        clientController.getPrinter().printServerMenu();
        int command = MyScanner.getInt(1, 5);
        HashSet<Ability> abilities = new HashSet<>();
        for (Role role : members.get(clientController.getUser().getUsername())) {
            abilities.addAll(role.getAbilities());
        }
        switch (command) {
            case 1 -> changeInfo(clientController, abilities);
            case 2 -> addOrRemoveMembers(clientController, abilities);
            case 3 -> addOrRemoveTextChannels(clientController, abilities);
            case 4 -> enterATextChannel(clientController, abilities);
        }
    }

    private void changeInfo(ClientController clientController, HashSet<Ability> abilities) throws IOException {
        clientController.getPrinter().printServerChangeInfoMenu();
        int command = MyScanner.getInt(1, 4);
        switch (command) {
            case 1 -> {
                if (abilities.contains(Ability.ChangeServerName)) {
                    clientController.getPrinter().printGetMessage("new server name");
                    serverName = MyScanner.getLine();
                    clientController.getPrinter().printSuccessMessage("server name change");
                } else {
                    clientController.getPrinter().printErrorMessage("server name change");
                }
            }
            case 2 -> {
                clientController.getPrinter().printTextChannelList(textChannels);
                int index = MyScanner.getInt(1, textChannels.size());
                clientController.getPrinter().printGetMessage("new text channel name");
                String newTextChannelName = MyScanner.getLine();
                textChannels.get(index).setName(newTextChannelName);
            }
            case 3 -> createOrEditARole(clientController);
        }
        clientController.getMySocket().write(new UpdateServerOnMainServerAction(this));
    }

    private void createOrEditARole(ClientController clientController) {
        if (clientController.getUser().getUsername().equals(owner)) {

            clientController.getPrinter().printRoleEditMenu();
            switch (MyScanner.getInt(1, 3)) {

                case 1 -> {
                    Role newRole = createNewRole(clientController);


                    clientController.getPrinter().println("Enter the usernames of the members you want to give this role to");
                    clientController.getPrinter().println("the usernames must be seperated by a space (invalid usernames will be ignored)");
                    clientController.getPrinter().printServerMembersList(members.keySet());

                    addInitialRoleHolders(newRole);
                }

                case 2 -> editARole();
            }


        } else {
            clientController.getPrinter().println("Only the owner can create a new role!");
        }
    }

    private Role createNewRole(ClientController clientController) {
        clientController.getPrinter().println("What is the name of the new role?");
        String newRoleName;
        do {
            newRoleName = MyScanner.getLine();
        } while ("".equals(newRoleName.trim()));
        clientController.getPrinter().println("What abilities does this role have?");
        clientController.getPrinter().printAbilityList();
        clientController.getPrinter().println("(enter the numbers seperated by a space)");
        ArrayList<Integer> abilityIndexes = clientController.getIntList(8);
        Role newRole = new Role(newRoleName, new HashSet<>());
        for (int abilityIndex : abilityIndexes) {
            newRole.getAbilities().add(Ability.values()[abilityIndex - 1]);
        }
        serverRoles.put(newRole.getRoleName(), newRole);
        clientController.getPrinter().printSuccessMessage("new role");
        return newRole;
    }

    private void addInitialRoleHolders(Role newRole) {
        String input = MyScanner.getLine();
        String[] initialRoleHolders = input.split(" ");
        for (String member : initialRoleHolders) {
            member = member.trim();
            if (members.containsKey(member)) {
                members.get(member).add(newRole);
            }
        }
    }

    private void editARole() {

    }

    private void addOrRemoveMembers(ClientController clientController, HashSet<Ability> abilities) {

    }

    private void addOrRemoveTextChannels(ClientController clientController, HashSet<Ability> abilities) {

    }

    private void enterATextChannel(ClientController clientController, HashSet<Ability> abilities) {
        clientController.getPrinter().printTextChannelList(textChannels);
        int index = MyScanner.getInt(1, textChannels.size()) - 1;
        textChannels.get(index).enter();
    }
}
