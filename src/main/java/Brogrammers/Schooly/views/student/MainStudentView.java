package Brogrammers.Schooly.views.student;

import Brogrammers.Schooly.Entity.AssignmentStudentview;
import Brogrammers.Schooly.Entity.gradeStudentView;
import Brogrammers.Schooly.Repository.AssignmentStudentviewRepository;
import Brogrammers.Schooly.Repository.GradeStudentRepository;
import Brogrammers.Schooly.views.AppLayoutNavbarPlacementStudent;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.grid.Grid;

import data.entity.Stu_AssignmentDetailsFormLayout;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.security.RolesAllowed;

/**
 *
 * @author evanc
 */
@Route(value = "student", layout= AppLayoutNavbarPlacementStudent.class)
@PageTitle("Dashboard | Schooly")
@RolesAllowed("ROLE_STUDENT")

public class MainStudentView extends VerticalLayout {

    /**
     * Assignment Grid 
     */
    protected Grid<AssignmentStudentview> assGrid = new Grid<>(AssignmentStudentview.class, false);

    /**
     * Grade Grid
     */
    protected Grid<gradeStudentView> gradeGrid = new Grid<>(gradeStudentView.class, false);

    /**
     * Display current page
     */
    protected H2 currentPage = new H2("Dashboard");

    /**
     * Identify Grid
     */
    protected H3 assTitle = new H3("Assignments");

    /**
     * Identify Grid
     */
    protected H3 gradeTitle = new H3("Grades");
    AssignmentStudentviewRepository assignmentRepository;
    GradeStudentRepository gradeRepo;

    /**
     * This main function creates the dashboard of the student view. It gets and
     * displays the name of the current user, formats the grid and grid titles
     * accordingly, and populates the grids with their respective information.
     * @param assignmentRepository DB view with relative assignment information
     * @param gradeRepo DB view with relative grade information
     */
    public MainStudentView(AssignmentStudentviewRepository assignmentRepository, GradeStudentRepository gradeRepo) {
        this.assignmentRepository = assignmentRepository;
        this.gradeRepo = gradeRepo;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        H3 welcome = new H3("Welcome " + username + "!");
        welcome.getStyle().set("margin-top", "0px");

        //configure main page
        addClassName("student-main-view");
        setSpacing(false);
        getThemeList().add("spacing-s");
        setSizeFull();
        configHeader();
        //layout
        HorizontalLayout hlayout = new HorizontalLayout(assGrid, gradeGrid); // Grids for Assingments and Grades
        HorizontalLayout textLayout = new HorizontalLayout(assTitle, gradeTitle); // Titles above respective grid

        //add above to vertical layout
        VerticalLayout page = new VerticalLayout(textLayout);

        assTitle.setWidth("100%");
        gradeTitle.setWidth("100%");
        textLayout.setVerticalComponentAlignment(Alignment.CENTER);

        //Configure Page
        page.setWidth("100%");
        page.setPadding(false);
        page.setSpacing(false);
        page.getThemeList().add("spacing-xs");

        configureGrid();


        //add items to grids
        //drop-down for assignment grid
        assGrid.setItemDetailsRenderer(createAssignmentDetailRenderer());

        textLayout.setPadding(true);
        textLayout.setWidth("100%");

        hlayout.setPadding(true);
        hlayout.setWidth("100%");

        welcome.getStyle().set("color", "hsl(214, 100%, 43%)");

        add(currentPage, welcome, new Hr(), page, hlayout);
        updateGrid();
    }

    private ComponentRenderer<Stu_AssignmentDetailsFormLayout, AssignmentStudentview> createAssignmentDetailRenderer() {
        return new ComponentRenderer<>(Stu_AssignmentDetailsFormLayout::new, Stu_AssignmentDetailsFormLayout::setAssignment);
    }


    private void configHeader() {

        HorizontalLayout header = new HorizontalLayout(
                currentPage
        );
        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER
        );
        header.setAlignSelf(FlexComponent.Alignment.AUTO);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-s");
    }

    private void configureGrid() {
        //Assignment Grid
        assGrid.addClassNames("assignment-grid");
        assGrid.setWidth("Auto");
        assGrid.addColumn(AssignmentStudentview::getCourseName).setHeader("Course");
        assGrid.addColumn(AssignmentStudentview::getAssignmentName).setHeader("Assignment");
        assGrid.addColumn(AssignmentStudentview::getDueDate).setHeader("Due Date");
        assGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        assGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        //Grade Grid
        gradeGrid.addClassNames("grade-grid");
        gradeGrid.setWidth("Auto");
        gradeGrid.addColumn(gradeStudentView::getCourseName).setHeader("Course");
        gradeGrid.addColumn(gradeStudentView::getAssignmentName).setHeader("Assignment");
        gradeGrid.addColumn(gradeStudentView::getGrade).setHeader("Grade");
        gradeGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        gradeGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES,
            GridVariant.MATERIAL_COLUMN_DIVIDERS);
    }
    private void updateGrid() {
        assGrid.setItems(assignmentRepository.findAll());
        gradeGrid.setItems(gradeRepo.search());
    }
}
