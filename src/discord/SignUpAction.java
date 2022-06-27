package discord;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpAction extends Action {

    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private int stage;

    public int getStage() {
        return stage;
    }

    public void finalizeStage() {
        stage = 4;
    }

    public void setUsername(String username) {
        this.username = username;
        stage = 0;
    }

    public void setPassword(String password) {
        this.password = password;
        stage = 1;
    }

    public void setEmail(String email) {
        this.email = email;
        stage = 2;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        stage = 3;
    }

    @Override
    public Object act() {
        switch (stage) {
            case 0 -> {
                String regex = "^[A-Za-z0-9]{6,}$";
                return !MainServer.getUsers().containsKey(username) && isMatched(regex, username);
            }
            case 1 -> {
                String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d+]{8,}$";
                return isMatched(regex, password);
            }
            case 2 -> {
                try {
                    String[] inputs = email.split("@");
                    String[] afterAtSign = inputs[1].split("\\.");
                    String reg = "^[A-Za-z0-9]*$";
                    return isMatched(reg, inputs[0]) && isMatched(reg, afterAtSign[0]) && isMatched(reg, afterAtSign[1]);
                } catch (Exception e) {
                    return false;
                }
            }
            case 3 -> {
                if ("0".equals(phoneNumber)) {
                    phoneNumber = null;
                    return true;
                } else {
                    String reg = "^[0-9]{11,}$";
                    return isMatched(reg, phoneNumber);
                }

            }
            case 4 -> {
                Model newUser = new Model(username, password, email, phoneNumber);
                MainServer.signUpUser(newUser);
                return newUser;
            }
        }
        return null;
    }

    private static boolean isMatched(String regex, String input) {
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(input);
        return mt.matches();
    }
}