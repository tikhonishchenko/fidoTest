package fido;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@RestController
public class FirstRestController {
    private static RestResponse userInfo = null;

    public static class RestResponse{
        private boolean userHasLoggedIn = false;
        private String callAnswer = "";
        public boolean getUserHasLoggedIn() {
            return userHasLoggedIn;
        }

        public void setUserHasLoggedIn(boolean userHasLoggedIn) {
            this.userHasLoggedIn = userHasLoggedIn;
        }

        public void checkRegisterUser(String name, String email, String password){
            if(!email.equals("") && !password.equals("") && !name.equals("")){
                authRegisterUser(name, email, password);
            }
            else{
                System.out.println("Wrong login info");
            }
        }
        public void checkDeleteUser(String email, String password){
            if(!email.equals("") && !password.equals("")){
                authDeleteUser(email, password);
            }
            else{
                System.out.println("Wrong login info");
            }
        }

        public void checkLoginUser(String email, String password){
            if(!email.equals("") && !password.equals("")){
                authLoginUser(email, password);
            }
            else{
                System.out.println("Wrong login info");
            }
        }
        private  void authLoginUser(String loginEmail, String loginPassword){
            DatabaseHandler dbHandler = new DatabaseHandler();
            ResultSet result = null;
            try {
                  result = dbHandler.getUser(loginEmail, loginPassword);

            } catch (Exception e) {
                e.printStackTrace();
            }

            int counter = 0;
            try
            {

                while (result.next()){

                    counter++;
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }

            if (counter>=1){
                setUserHasLoggedIn(true);
                userInfo = this;
                System.out.println("User found!");
            }
            else{
                System.out.println("No user found!");
            }
        }

        private  void authRegisterUser(String registerName, String registerEmail, String registerPassword){
            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.signUpUser(registerName, registerEmail, registerPassword);
        }
        private  void authDeleteUser(String registerEmail, String registerPassword){
            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.deleteUser(registerEmail, registerPassword);
        }
        public void checkCreateRoom(String roomName, String location, int capacity){
            if(!roomName.equals("") && capacity<=100 && capacity>0){
                authCreateRoom(roomName, location, capacity);
            }
            else{
                System.out.println("Wrong room info");
            }
        }
        private  void authCreateRoom(String roomName, String location, int capacity){
            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.createRoom(roomName, location, capacity);
        }
        public void checkDeleteRoom(String roomName){
            if(!roomName.equals("")){
                authDeleteRoom(roomName);
            }
            else{
                System.out.println("Wrong login info");
            }
        }
        private  void authDeleteRoom(String roomName){
            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.deleteRoom(roomName);
        }

        private  void checkRoomAvailability(String startStringDate, String endStringDate){
            DatabaseHandler dbHandler = new DatabaseHandler();
            ResultSet result = null;
            int counter = 0;
            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("dd HH:mm").parse(startStringDate));
                Timestamp startDate = new Timestamp(cal.getTimeInMillis());
                System.out.println(startDate);
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(new SimpleDateFormat("dd HH:mm").parse(endStringDate));

                Timestamp endDate = new Timestamp(cal2.getTimeInMillis());
                System.out.println(endDate);
                if(endDate.before(startDate)) {
                    result = dbHandler.checkRoomAvailability(endDate, startDate);
                }
                else{
                    result = dbHandler.checkRoomAvailability(startDate, endDate);
                }
                if(result.next()){
                    counter++;
                }

                System.out.println(counter);
                if (counter>=1){
                    System.out.println("time found!");
                }
                else{
                    dbHandler.rentTime(startDate,endDate);
                    System.out.println("clear to write!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    public static class Date{

        public static String setDate(int startDay, int startHours, int startMinutes) {
            String date = startDay + " " + startHours + ":" + startMinutes;
            return date;
        }

    }
    @RequestMapping(path = "/register/{name}/{email}/{password}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse registerUser(@PathVariable String name, @PathVariable String email,@PathVariable String password ){
        RestResponse result = new RestResponse();

        result.checkRegisterUser(name, email, password);
        loginUser(email, password);
        result = userInfo;
        return result;
    }
    @RequestMapping(path = "/login/{email}/{password}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public static RestResponse loginUser(@PathVariable String email, @PathVariable String password){
        RestResponse result = new RestResponse();

        result.checkLoginUser(email, password);
        return result;
    }
    @RequestMapping(path = "/rentRoom/{startDay}-{startHours}-{startMinutes}/{finalDay}-{finalHours}-{finalMinutes}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public static RestResponse rentRoom(
            @PathVariable int startDay,@PathVariable int startHours, @PathVariable int startMinutes, @PathVariable int finalDay,@PathVariable int finalHours, @PathVariable int finalMinutes){
        RestResponse result = null;
        if(userInfo!=null){
            result = userInfo;
        }
        else{
            result = new RestResponse();
        }


        if(result.userHasLoggedIn){
            result.checkRoomAvailability(Date.setDate(startDay, startHours, startMinutes), Date.setDate(finalDay, finalHours,finalMinutes));
        }
        return result;
    }
    @RequestMapping(path = "/deleteUser/{email}/{password}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse deleteUser(@PathVariable String email,@PathVariable String password ){
        RestResponse result = new RestResponse();

        result.checkDeleteUser(email, password);
        return result;
    }

    @RequestMapping(value="/createRoom", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse createRoom(@RequestParam String roomName, @RequestParam Optional<String> location, @RequestParam int capacity){
        RestResponse result = null;
        if(userInfo!=null){
            result = userInfo;
        }
        else{
            result = new RestResponse();
        }
        if(result.getUserHasLoggedIn()){
            result.checkCreateRoom(roomName, location.toString(), capacity);
        }
        return result;
    }
    @RequestMapping(value="/deleteRoom", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse deleteRoom(@RequestParam String roomName){
        RestResponse result = null;
        if(userInfo!=null){
            result = userInfo;
        }
        else{
            result = new RestResponse();
        }
        if(result.getUserHasLoggedIn()){
            result.checkDeleteRoom(roomName);
        }
        return result;
    }


}
