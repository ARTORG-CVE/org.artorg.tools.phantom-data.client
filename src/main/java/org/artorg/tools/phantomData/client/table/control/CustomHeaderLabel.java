package org.artorg.tools.phantomData.client.table.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class CustomHeaderLabel extends BorderPane {

    protected Label customNode = null;
    BooleanProperty expanded = new SimpleBooleanProperty(this, "expanded", false);

    public CustomHeaderLabel(final Label parent) {
        Label label = new Label(parent.getText());

        // custom MenuButton
        Button btn = new Button();
        btn.setGraphic(new Label("\u2261"));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent ae) {
                System.out.println("Hello World");
            }

        });

        TextField filterTextField = new TextField();
        filterTextField.promptTextProperty().set("type here to filter");

        setCenter(label);
        setRight(btn);
        setBottom(filterTextField);

        EventHandler<MouseEvent> toggleHeader = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                expanded.set(!expanded.get());
            }

        };
        parent.setOnMouseClicked(toggleHeader);
        expanded.addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean value) {
                showCustomHeader(value);
            }

        });

        label.textProperty().bind(parent.textProperty());
        parent.setGraphic(this);

        customNode = parent;
        showCustomHeader(expanded.get());
    }

    protected void showCustomHeader(Boolean value) {
        if (value) {
            customNode.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            customNode.setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}

