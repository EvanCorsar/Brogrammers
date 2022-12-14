package Brogrammers.Schooly.views.student;


import Brogrammers.Schooly.Entity.gradeStudentView;
import Brogrammers.Schooly.Repository.GradeStudentRepository;
import Brogrammers.Schooly.views.AppLayoutNavbarPlacementStudent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


import javax.annotation.security.RolesAllowed;

/**
 *
 * @author evanc
 */
@Route(value = "grades", layout = AppLayoutNavbarPlacementStudent.class)
@PageTitle("Grades | Schooly")
@RolesAllowed("ROLE_STUDENT")
public class StudentGradeView extends VerticalLayout {

    /**
     * Grid of student grades
     */
    protected Grid<gradeStudentView> grid = new Grid<>(gradeStudentView.class, false);

    /**
     * H2 to display current page title
     */
    protected H2 currentPage = new H2("Grades");
    GradeStudentRepository courseRepo;

    /**
     * This main function will configure the grid, add all items to the page,
     * and populate all data for the grid.
     * @param courseRepo view within the DB to fetch needed information
     */
    public StudentGradeView(GradeStudentRepository courseRepo) {
        //this.studAssignRepository = studAssignRepository;
        this.courseRepo = courseRepo;
        addClassName("student-grade-view");
        setSizeFull();
        configureGrid();

        add(currentPage, new Hr(), grid);
        updateGrid();

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
        grid.addClassName("grade-grid");
        grid.setSizeFull();

        grid.addColumn(gradeStudentView::getAssignmentName).setHeader("Assignment");
        grid.addColumn(gradeStudentView::getGrade).setHeader("Grade");
        grid.addColumn(gradeStudentView::getCourseName).setHeader("Course");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setSortableColumns();
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS,
                GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
    }
    private void updateGrid() {
        grid.setItems(courseRepo.search());
    }

}
