package com.enginizer.ui;

import com.enginizer.model.Customer;
import com.enginizer.service.CustomerService;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.BigDecimalRangeValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.themes.ValoTheme;

import java.math.BigDecimal;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

    @Autowired
    private CustomerService customerService;

    /**
     * The currently edited customer
     */
    private Customer customer;

    /* Fields to edit properties in Customer entity */
    TextField name;
    TextField phone;
    TextField email;

    /* Action buttons */
    Button saveButton = new Button("Save", FontAwesome.SAVE);
    Button cancelButton = new Button("Cancel");
    CssLayout actions = new CssLayout(saveButton, cancelButton);

    public CustomerEditor() {
        setSpacing(true);

        name = buildNameField();
        phone = buildPhoneField();
        email = buildEmailField();


        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(name, phone, email);
        formLayout.addComponent(actions);

        addComponent(formLayout);

        // Configure and style components

        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);

        // wire action buttons to saveButton, deleteButton and reset
        saveButton.addClickListener(e -> preSubmit(e));
        saveButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        saveButton.addStyleName("v-saveButton-button");
        cancelButton.addClickListener(e -> editCustomer(customer));
        setVisible(false);
    }

    private TextField buildEmailField() {
        email = new TextField("Email");
        email.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        email.addStyleName("v-textfield-cool");
        email.addValidator(new EmailValidator("Not a valid email"));
        email.setRequired(true);
        email.setBuffered(true);
        return email;
    }

    private TextField buildPhoneField() {
        phone = new TextField("Phone number");
        phone.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        phone.addStyleName("v-textfield-cool");
        phone.setRequired(true);
        phone.setBuffered(true);
        phone.addValidator(new RegexpValidator("[0-9]{1,10}", "Not a valid phone number"));
        return phone;
    }

    private TextField buildNameField() {
        name = new TextField("Name");

        name.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        name.addStyleName("v-textfield-cool");
        name.setRequired(true);
        name.setBuffered(true);
        name.addValidator(new StringLengthValidator(
                "The name must be 1-10 letters (was {0})",
                1, 10, true));

        return name;
    }

    private void preSubmit(Button.ClickEvent event) {
        try {
            name.validate();
            phone.validate();
            email.validate();
            customerService.save(customer);
            setVisible(false);
        } catch (Validator.InvalidValueException e) {
            setVisible(true);
            Notification.show(e.getMessage());
        }
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editCustomer(Customer c) {
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            customer = customerService.findOne(c.getId());
        } else {
            customer = c;
        }
        cancelButton.setVisible(persisted);
        cancelButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        try {
            BeanFieldGroup.bindFieldsUnbuffered(customer, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setVisible(true);

        // A hack to ensure the whole form is visible
        saveButton.focus();
        // Select all text in name field automatically
        name.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either saveButton or deleteButton
        // is clicked
        saveButton.addClickListener(e -> h.onChange());
    }

}
