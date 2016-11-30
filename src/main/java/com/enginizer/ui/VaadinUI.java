package com.enginizer.ui;

import com.enginizer.model.Customer;
import com.enginizer.service.CustomerService;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@SpringUI
@Theme("custom")
public class VaadinUI extends UI {

    @Autowired
    private CustomerService customerService;

    private final CustomerEditor editor;

    final Grid grid;

    final TextField filter;

    private final Button addNewBtn;

    private Button deleteButton;


    @Autowired
    public VaadinUI(CustomerEditor editor) {
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("Add entry", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {
        deleteButton = buildDeleteButton();

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, deleteButton);

        HorizontalLayout horizontalLayout = new HorizontalLayout(grid, editor);
        VerticalLayout mainLayout = new VerticalLayout(actions, horizontalLayout);

        setContent(mainLayout);

        // Configure layouts and components
        actions.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("id", "name", "phone", "email");
        grid.addStyleName(ValoTheme.TABLE_BORDERLESS);

        filter.setInputPrompt("Filter by name");
        filter.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        filter.addStyleName("v-textfield-cool");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(e -> listCustomers(e.getText()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addSelectionListener(e -> {
            if (e.getSelected().isEmpty()) {
                editor.setVisible(false);
            } else {
                editor.editCustomer((Customer) grid.getSelectedRow());
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "", "")));
        addNewBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        addNewBtn.addStyleName("v-button-cool");


        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    private Button buildDeleteButton() {
        deleteButton = new Button("Delete", FontAwesome.TRASH_O);
        deleteButton.addClickListener(event -> handleDelete());
        deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        deleteButton.addStyleName("v-delete-button");
        return deleteButton;
    }

    private void handleDelete() {
        Customer selectedRow = (Customer) grid.getSelectedRow();

        if (null != selectedRow) {
            customerService.delete(selectedRow);
            listCustomers(filter.getValue());
        }
    }

    // tag::listCustomers[]
    void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setContainerDataSource(new BeanItemContainer(Customer.class, customerService.findAll()));
        } else {
            grid.setContainerDataSource(new BeanItemContainer(Customer.class, customerService.findByNameStartsWithIgnoreCase(text)));
        }
    }
    // end::listCustomers[]

}
