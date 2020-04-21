/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tunisiagottalent.UI.User;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.glyphfont.FontAwesome;
import tunisiagottalent.Entity.Subscription;
import tunisiagottalent.Entity.User;
import tunisiagottalent.Entity.video;
import tunisiagottalent.UI.Base.MainController;
import tunisiagottalent.services.SubscriptionServices;
import tunisiagottalent.services.UserServices;
import tunisiagottalent.services.VideoServices;
import tunisiagottalent.services.VoteServices;
import tunisiagottalent.util.UserSession;

/**
 * FXML Controller class
 *
 * @author alaa
 */
public class ProfileController {

    @FXML
    private Label talented;

    @FXML
    private Label admin;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView profilePic;

    @FXML
    private Label username_profile;

    @FXML
    private Label nameLastName;

    @FXML
    private Label bio;

    @FXML
    private ScrollPane videos_Container;

    @FXML
    private Button subscribeButton;
    @FXML
    private Button btn_add_vid;

    @FXML
    private Button btn_edit;
    @FXML
    private VBox vboxvids;
    @FXML
    private Button unsubscribeButton;
    private User user;
    SubscriptionServices ss = new SubscriptionServices();
    VideoServices vs = new VideoServices();
    @FXML
    private Label subs;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            loadInfo();
            loadVideos();
            subs.setText(String.valueOf(ss.getsubs(user)));
        });

    }

    @FXML
    void loadInfo() {

        subscribeButton.setVisible(false);
        unsubscribeButton.setVisible(false);
        UserServices us = new UserServices();
        SubscriptionServices ss = new SubscriptionServices();
        UserSession s = UserSession.instance;

        if (user.getRole().contains("ROLE_TALENTED")) {
            talented.setVisible(true);
        } else if (user.getRole().contains("ROLE_ADMIN")) {
            admin.setVisible(true);
        }
        Rectangle clip = new Rectangle(
                profilePic.getFitWidth(), profilePic.getFitHeight()
        );
        clip.setArcWidth(300);
        clip.setArcHeight(300);
        profilePic.setEffect(new DropShadow(20, Color.BLACK));
        profilePic.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = profilePic.snapshot(parameters, null);

        //remove the rounding clip so that our effect can show through.
        //profilePic.setClip(null);
        // apply a shadow effect.
//        System.out.println(user);
        username_profile.setText(user.getUsername());

        profilePic.setImage(new Image("http://127.0.0.1:8000/assets/uploads/" + user.getProfilePic()));
        if ((us.getUser(user.getUsername()).getName() == null || us.getUser(user.getUsername()).getLastName() == null) ) {
           if((s.getU().getUsername().equals(user.getUsername())))
            nameLastName.setText("Complete your profile !!");
           else nameLastName.setText("This user Has not Completed his profile");
        } else {
            nameLastName.setText(us.getUser(user.getUsername()).getName() + " " + us.getUser(user.getUsername()).getLastName());
        }
        bio.setText(us.getUser(user.getUsername()).getBio());
        if (!s.getU().getUsername().equals(username_profile.getText())) {

            btn_edit.setVisible(false);
            if (ss.exists(user.getId(), s.getU().getId())) {
                unsubscribeButton.setVisible(true);
            } else {
                subscribeButton.setVisible(true);
            }
        }
    }

    @FXML
    void editProfile(ActionEvent event) {
        try {
            AnchorPane p = FXMLLoader.load(getClass().getResource("/tunisiagottalent/UI/User/editProfile.fxml"));
            rootPane.getChildren().setAll(p);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    void addVideo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tunisiagottalent/UI/Video/addVideo.fxml"));
            Parent third = loader.load();
            Scene s = new Scene(third);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setOpacity(1);
            stage.setTitle("Add Video");
            stage.initOwner(rootPane.getScene().getWindow());
            stage.setScene(s);

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void loadVideos() {

        List<video> tabs = vs.getVideos(user.getId());
        VoteServices votesserv = new VoteServices();
        tabs.forEach((vid) -> {
            HBox video_grid = new HBox();
            VBox details = new VBox();

            Label Title = new Label(vid.getTitle());
            Title.setTextFill(javafx.scene.paint.Color.WHITE);
            Title.setFont(Font.font("Cambria", 22));

            Label VoteNum = new Label(Integer.toString(votesserv.getVotes(vid)));
            VoteNum.setTextFill(javafx.scene.paint.Color.WHITE);
            VoteNum.setFont(Font.font("Cambria", 24));

            Label pubDate = new Label(vid.getPublish_date().toString());
            pubDate.setTextFill(javafx.scene.paint.Color.WHITE);
            pubDate.setFont(Font.font("Cambria", 16));

            JFXButton Delete = new JFXButton("Delete");
            Delete.setVisible(false);
            Delete.resize(150, 250);
            Delete.setStyle("-fx-text-fill: white;-fx-font-size:18px;-fx-background-color:#49111C");

            WebView preview = new WebView();
            preview.getEngine().load(vid.getUrl());
            
            Label labelheart = new Label();
            Label labelunheart = new Label();
            labelheart.setGraphic(new FontAwesome().create(FontAwesome.Glyph.HEART).color(Color.RED).size(20));
            labelunheart.setGraphic(new FontAwesome().create(FontAwesome.Glyph.HEART).color(Color.WHITE).size(20));
            VoteNum.setGraphic(new FontAwesome().create(FontAwesome.Glyph.THUMBS_UP).color(Color.GREEN).size(20));
            VoteNum.setAlignment(Pos.CENTER);
            JFXToggleButton Voting = new JFXToggleButton();
            Voting.setToggleLineColor(Color.RED);
            Voting.setToggleColor(Color.RED);
            HBox hboxvoting = new HBox();

            hboxvoting.setAlignment(Pos.CENTER);
            UserSession s = UserSession.instance;
            if (s.getU().getUsername().equals(vid.getOwner().getUsername())) {

                Delete.setVisible(true);

            }
            if (votesserv.find(vid, s.getU())) {
                Voting.setSelected(true);
                labelheart.setVisible(true);
                labelunheart.setVisible(false);
            } else {
                Voting.setSelected(false);
                labelunheart.setVisible(true);
                labelheart.setVisible(false);
            }
            Voting.setOnAction((e) -> {
                if (Voting.isSelected()) {

                    votesserv.Add(s.getU(), vid);

                    Platform.runLater(() -> {
                        VoteNum.setText(Integer.toString(Integer.parseInt(VoteNum.getText()) + 1));
                        labelunheart.setVisible(false);
                        labelheart.setVisible(true);

                    });

                } else {

                    votesserv.delete(vid, s.getU());

                    Platform.runLater(() -> {
                        labelheart.setVisible(false);
                        labelunheart.setVisible(true);
                        VoteNum.setText(Integer.toString(Integer.parseInt(VoteNum.getText()) - 1));

                    });
                }
            });

            Delete.setOnAction((ActionEvent event) -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Delete Video");
                alert.setHeaderText("Are you sure ?");
                alert.setContentText("You will lose all your votes !");
                alert.showAndWait();
                vs.delete(vid);
                video_grid.getChildren().removeAll(preview, details);
                video_grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == tabs.indexOf(vid));
            });

            

            details.setStyle("-fx-background-color: #0c0527");
            details.setPadding(new Insets(15, 15, 15, 15));
            preview.setPrefHeight(300);
            details.setSpacing(30);
            details.setPrefWidth(300);
            vboxvids.setSpacing(25);
            hboxvoting.getChildren().addAll(labelunheart, Voting, labelheart);
            details.getChildren().addAll(Title, pubDate, Delete, hboxvoting, VoteNum);
            video_grid.getChildren().addAll(preview, details);
            vboxvids.getChildren().addAll(video_grid);

        });

    }

    @FXML
    void subsbribe(ActionEvent event) {

        UserSession s = UserSession.instance;
        Subscription sub = new Subscription();
        sub.setSub_id(s.getU().getId());
        sub.setSubetto_id(user.getId());
        LocalDate current = LocalDate.now();
        sub.setSubscription_date(Date.valueOf(current));
        subs.setText((Integer.toString(Integer.parseInt(subs.getText()) + 1)));
        if (ss.subscribe(sub)) {
            subscribeButton.setVisible(false);
            unsubscribeButton.setVisible(true);
        }
    }

    @FXML
    void unsubscribe(ActionEvent event) {

        UserServices us = new UserServices();

        UserSession s = UserSession.instance;

        Subscription sub = new Subscription();
        sub.setSub_id(s.getU().getId());
        sub.setSubetto_id(user.getId());
        subs.setText((Integer.toString(Integer.parseInt(subs.getText()) - 1)));
        if (ss.unsubscribe(sub)) {
            subscribeButton.setVisible(true);
            unsubscribeButton.setVisible(false);
        }
    }

}
