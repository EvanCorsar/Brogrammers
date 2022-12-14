package Brogrammers.Schooly.views.admin;

import Brogrammers.Schooly.Entity.Instructor;
import Brogrammers.Schooly.Repository.CourseRepository;
import Brogrammers.Schooly.Repository.InstructorRepository;
import Brogrammers.Schooly.views.SecurityService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.json.JSONException;


import javax.annotation.security.RolesAllowed;

import java.io.IOException;

import static Brogrammers.Schooly.APIAndOtherMethods.getWeather;
import static java.lang.Boolean.TRUE;

@PageTitle("Admin-Instructor")
@Route("/Admin/instructor")
@RolesAllowed("ROLE_ADMIN")
public class InstructorList extends VerticalLayout {

    private final SecurityService securityService;
    InstructorRepository instructorRepository;
    CourseRepository courseRepository;
    Grid<Instructor> grid = new Grid<>();

    TextField search = new TextField();
    ModifyFormInstructor form;


    public InstructorList(SecurityService securityService, InstructorRepository instructorRepository, CourseRepository courseRepository) throws JSONException {
        this.securityService = securityService;
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        setSizeFull();
        gridConfigure();
        formConfigure();
        String api = "Welcome admin! " + getWeather();
        HorizontalLayout headers = new HorizontalLayout(new H2("Schooly"), new H6(api));
        headers.setAlignItems(Alignment.BASELINE);
        headers.setSpacing(TRUE);
        headers.setMargin(TRUE);

        add(headers, toolbarConfigure(), gridForm());

        updateGrid();
        closeForm();
    }

    /**
     * The method hides the form so that the grid can have more space.
     */
    private void closeForm() {
        form.setInstructor(null);
        form.setVisible(false);
    }

    /**
     * This function is called to populate the grid or refresh the grid.
     */
    private void updateGrid() {
        grid.setItems(instructorRepository.search(search.getValue()));
    }

    /**
     * This method sets up the form and grid in a horizontal format, form and grid are next to each other.
     */
    private Component gridForm() {
        HorizontalLayout gridForm = new HorizontalLayout(grid, form);
        gridForm.setFlexGrow(2, grid);
        gridForm.setFlexGrow(1, form);
        gridForm.setSizeFull();
        return gridForm;
    }

    /**
     * This is where we instantiate the form.
     */
    private void formConfigure() {
        form = new ModifyFormInstructor(this.courseRepository);
        form.setWidth("25em");

        form.addListener(ModifyFormInstructor.SaveEvent.class, this::saveInstructor);
        form.addListener(ModifyFormInstructor.DeleteEvent.class, this::deleteInstructor);
        form.addListener(ModifyFormInstructor.CloseEvent.class, e -> closeForm());
    }

    /**
     * This function is used to delete the passed instructor from the database.
     * @param event
     */
    private void deleteInstructor(ModifyFormInstructor.DeleteEvent event) {
        instructorRepository.delete(event.getInstructor());
        updateGrid();
        closeForm();
    }

    /**
     * This function is used to save the passed instructor into the database.
     * @param event
     */
    private void saveInstructor(ModifyFormInstructor.SaveEvent event) {
        instructorRepository.save(event.getInstructor());
        updateGrid();
        closeForm();
    }

    /**
     * This function configures the buttons that are used to navigate through admin accessible views, add and search instructors.
     */
    private Component toolbarConfigure() {
        search.setPlaceholder("Search...");
        search.setClearButtonVisible(true);
        search.setValueChangeMode(ValueChangeMode.LAZY);
        search.addValueChangeListener(e -> updateGrid());

        Button studNavigationButton = new Button("Student", event-> UI.getCurrent().navigate("/Admin/student"));
        Button courseNavigationButton = new Button("Course", event-> UI.getCurrent().navigate("/Admin/course"));
        Button studCourseNavigationButton = new Button("Student Course", event-> UI.getCurrent().navigate("/Admin/student_course"));
        Button addInstructorButton = new Button("Add Instructor");
        Button logout = new Button("Log out", e -> this.securityService.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_ERROR);
        addInstructorButton.addClickListener(e -> addInstructor());

        return new HorizontalLayout(studNavigationButton,courseNavigationButton, studCourseNavigationButton, search, addInstructorButton, logout);
    }

    /**
     * When the addInstructor button is clicked this functioned is called which clears the selected item in grid
     * and calls the editInstructor function
     */
    private void addInstructor() {
        grid.asSingleSelect().clear();
        editInstructor(new Instructor());
    }

    /**
     * This function populates the form fields with the instructor that was passed by the calling function.
     * @param instructor
     */
    private void editInstructor(Instructor instructor) {
        if(instructor == null){
            closeForm();
        }
        else{
            form.setInstructor(instructor);
            form.setVisible(true );
        }
    }

    /**
     * This function configures the grid, setting up the columns and designs.
     * When a grid element is clicked, the form is populated by this function so that we can edit the selected element.
     */
    private void gridConfigure() {
        grid.setSizeFull();
        grid.addColumn(Instructor::getId).setHeader("Instructor ID");
        grid.addColumn(Instructor::getFName).setHeader("First Name");
        grid.addColumn(Instructor::getLName).setHeader("Last Name");
        grid.addColumn(Instructor::getEmail).setHeader("Email");
        grid.addColumn(Instructor:: getCourseID).setHeader("Course ID");
        grid.addColumn(Instructor::getCourseName).setHeader("Course Name");

        grid.getColumns().forEach(col -> col.setAutoWidth(TRUE));
        grid.asSingleSelect().addValueChangeListener(e -> editInstructor(e.getValue()));
    }


}
