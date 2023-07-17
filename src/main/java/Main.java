// import Controller.SocialMediaController;
// //import Util.ConnectionUtil;
// import io.javalin.Javalin;
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// //import java.sql.Connection;
// //import java.io.IOException;
// // import com.fasterxml.jackson.databind.ObjectMapper;
// // import Model.Account;

// /**
//  * This class is provided with a main method to allow you to manually run and test your application. This class will not
//  * affect your program in any way and you may write whatever code you like here.
//  */
// public class Main {
//     public static void main(String[] args) {            

//         SocialMediaController controller = new SocialMediaController();
//         HttpClient webClient;
//         webClient = HttpClient.newHttpClient();

//         // Connection connection = ConnectionUtil.getConnection();

//         // if(connection != null)
//         // {
//         //     System.out.println("Connected to database");
//         //     Javalin app = controller.startAPI();

//         //     app.stop();
//         // }
//         // else
//         // {
//         //     System.out.println("Failed to connect to database");
//         // }
        
//         // Start the API and get the Javalin app
//         Javalin app = controller.startAPI();
        
        

//         // Make an HTTP POST request to register a new user
//         HttpRequest postRequest = HttpRequest.newBuilder()
//                 .uri(URI.create("http://localhost:8080/register"))
//                 .POST(HttpRequest.BodyPublishers.ofString("{" +
//                         "\"username\": \"user\", " +
//                         "\"password\": \"password\" }"))
//                 .header("Content-Type", "application/json")
//                 .build();
        
        
//         try {
        
//             HttpResponse response = webClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
//             int status = response.statusCode();
//             if (status == 200) {
//                 System.out.println("User registration successful.");
//                 System.out.println("Response body: " + response.body());
//             } else {
//                 System.out.println("User registration failed.");
//                 System.out.println("Response body: " + response.body());
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
        
//         //Stop the Javalin app
//         app.stop();
//         // SocialMediaController controller = new SocialMediaController();
//         // Javalin app = controller.startAPI();
//         // app.start(8080);
//     }
// }
