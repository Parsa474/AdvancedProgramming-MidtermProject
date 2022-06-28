package discord;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpAction implements Action {

    private String username;
    private String newUsername;     //used for changing username
    private String password;
    private String email;
    private String phoneNumber;
    private int stage;
    // when signing up always constructed 0
    // 1-4 for getting newUser fields when signing up, 5 for finalizing the signUp
    // -1 for changing one of the fields
    private int subStage;   //only actually used for stage == 6 (subStage always equals stage)

    private String regex;

    public SignUpAction() {
        stage = 0;
    }

    public SignUpAction(String username) {
        this.username = username;
        this.stage = -1;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public int getStage() {
        return stage;
    }

    public int getSubStage() {
        return subStage;
    }

    public void finalizeStage() {
        stage = 5;
    }

    public void setUsername(String username) {
        if (stage == 0) {
            this.username = username;
            stage = 1;
        } else {
            newUsername = username;
            subStage = 1;
        }
        regex = "^[A-Za-z0-9]{6,}$";
    }

    public void setPassword(String password) {
        this.password = password;
        if (stage != -1) {
            stage = 2;
        } else {
            subStage = 2;
        }
        regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
    }

    public void setEmail(String email) {
        this.email = email;
        if (stage != -1) {
            stage = 3;
        } else {
            subStage = 3;
        }
        regex = "^[A-Za-z0-9]*$";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (stage != -1) {
            stage = 4;
        } else {
            subStage = 4;
        }
        regex = "^[0-9]{11,}$";
    }

    @Override
    public Object act() {
        switch (stage) {
            case 1 -> {
                return !MainServer.getUsers().containsKey(username) && isMatched(username);
            }
            case 2 -> {
                return isMatched(password);
            }
            case 3 -> {
                return isAValidEmail(email);
            }
            case 4 -> {
                if ("0".equals(phoneNumber)) {
                    return true;
                } else {
                    return isMatched(phoneNumber);
                }
            }
            case 5 -> {
                if ("0".equals(phoneNumber)) phoneNumber = null;
                Model newUser = new Model(username, password, email, phoneNumber);
                MainServer.signUpUser(newUser);
                return newUser;
            }
            case -1 -> {
                boolean success = false;
                Model changedUser = MainServer.getUsers().get(username);
                switch (subStage) {
                    case 1 -> {
                        success = !MainServer.getUsers().containsKey(newUsername) && isMatched(newUsername);
                        if (success) changedUser.setUsername(newUsername);
                    }
                    case 2 -> {
                        success = isMatched(password);
                        if (success) changedUser.setPassword(password);
                    }
                    case 3 -> {
                        success = isAValidEmail(email);
                        if (success) changedUser.setEmail(email);
                    }
                    case 4 -> {
                        if ("0".equals(phoneNumber)) {
                            changedUser.setPhoneNumber(null);
                            return true;
                        }
                        success = isMatched(phoneNumber);
                        if (success) changedUser.setPhoneNumber(phoneNumber);
                    }
                }
                if (success) {
                    if (subStage == 1) {
                        MainServer.getUsers().remove(username);
                        MainServer.getUsers().put(newUsername, changedUser);
                        MainServer.deleteUserFromDataBase(username);
                    } else {
                        MainServer.getUsers().replace(username, changedUser);
                    }
                    MainServer.updateDatabase(changedUser);
                }
                return success;
            }
        }
        return null;
    }

    private boolean isMatched(String input) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(input);
        return mt.matches();
    }

    private boolean isAValidEmail(String email) {
        try {
            String[] inputs = email.split("@");
            String[] afterAtSign = inputs[1].split("\\.");
            return isMatched(inputs[0]) && isMatched(afterAtSign[0]) && isMatched(afterAtSign[1]);
        } catch (Exception e) {
            return false;
        }
    }
}