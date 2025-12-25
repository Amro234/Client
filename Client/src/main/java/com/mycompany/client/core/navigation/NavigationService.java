package com.mycompany.client.core.navigation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import com.mycompany.client.App;

public class NavigationService {

    private static Scene scene;
    private static final Deque<Parent> stack = new ArrayDeque<>();

    public static void init(Scene mainScene) {
        scene = mainScene;
    }

    public static void navigateTo(Parent next) {
        if (scene == null) {
            System.err.println("ERROR: NavigationService not initialized! Scene is null.");
            return;
        }
        if (next == null) {
            System.err.println("ERROR: Cannot navigate to null Parent!");
            return;
        }

        stack.push(scene.getRoot());
        scene.setRoot(next);
    }

    public static void replaceWith(Parent next) {
        if (scene == null) {
            System.err.println("ERROR: NavigationService not initialized! Scene is null.");
            return;
        }
        if (next == null) {
            System.err.println("ERROR: Cannot replace with null Parent!");
            return;
        }

        if (!stack.isEmpty()) {
            stack.pop();
            stack.push(next);
        }

        scene.setRoot(next);
    }

    public static void goBack() {
        if (!stack.isEmpty()) {
            scene.setRoot(stack.pop());
        }
    }

    public static void goBackAndReplace(Parent next) {
        if (!stack.isEmpty()) {
            stack.pop(); // Discard the previous screen
        }
        scene.setRoot(next);
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/client/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static Parent loadFXML(String fxml, String... css) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/mycompany/client/" + fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        if (css != null && css.length > 0) {
            for (String sheet : css) {
                root.getStylesheets().add(App.class.getResource("/styles/" + sheet + ".css").toExternalForm());
            }
        }
        return root;
    }

    public static FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(App.class.getResource("/com/mycompany/client/" + fxml + ".fxml"));
    }
}