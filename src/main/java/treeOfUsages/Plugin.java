package treeOfUsages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.intellij.ide.ActivityTracker;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.impl.source.PsiMethodImpl;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.action.CollapseTreeAction;
import treeOfUsages.action.ExpandTreeAction;
import treeOfUsages.action.FindUsagesAction;
import treeOfUsages.action.FindUsagesExcludingParentsAction;
import treeOfUsages.action.FindUsagesIncludingParentsAction;
import treeOfUsages.action.StopFindUsagesAction;
import treeOfUsages.util.TreeGenerator;

/**
 * Note that this warning may be logged when debugging and that it has no impact on IntelliJ or the plugin:
 * <a href="https://github.com/JetBrains/gradle-intellij-plugin/issues/777">
 *     Please call toolbar.setTargetComponent() explicitly
 * </a>
 */
public class Plugin
{
    private final JPanel generalPanel;

    private final JPanel bottomPanel;

    private final JBLoadingPanel loadingPanel;

    private final Project project;

    public Tree tree;

    private JBScrollPane treeView;

    private BackgroundableProcessIndicator progressIndicator;

    public boolean forcedCancel = false;

    // Actions
    private final FindUsagesAction findUsagesExcludingParentsAction = new FindUsagesExcludingParentsAction(this);

    private final FindUsagesAction findUsagesIncludingParentsAction = new FindUsagesIncludingParentsAction(this);

    private final StopFindUsagesAction stopFindUsagesAction = new StopFindUsagesAction(this);

    private final ExpandTreeAction expandTreeAction = new ExpandTreeAction(this);

    private final CollapseTreeAction collapseTreeAction = new CollapseTreeAction(this);


    public Plugin(Project p)
    {
        project = p;
        generalPanel = new JPanel(new BorderLayout());

        JComponent toolbarPanel = createToolbarPanel();
        generalPanel.add(toolbarPanel, BorderLayout.NORTH);

        bottomPanel = new JPanel(new BorderLayout());
        generalPanel.add(bottomPanel, BorderLayout.CENTER);

        treeView = new JBScrollPane();

        Disposable animationDisposable = Disposer.newDisposable();
        loadingPanel = new JBLoadingPanel(new FlowLayout(), animationDisposable);
        loadingPanel.startLoading();
    }

    private void setLoading(boolean isLoading)
    {
        bottomPanel.removeAll();

        if (isLoading)
        {
            bottomPanel.add(loadingPanel);

            findUsagesExcludingParentsAction.setEnabled(false);
            findUsagesIncludingParentsAction.setEnabled(false);
            stopFindUsagesAction.setEnabled(true);
            expandTreeAction.setEnabled(false);
            collapseTreeAction.setEnabled(false);
        }
        else
        {
            bottomPanel.add(treeView);

            findUsagesExcludingParentsAction.setEnabled(true);
            findUsagesIncludingParentsAction.setEnabled(true);
            stopFindUsagesAction.setEnabled(false);
            expandTreeAction.setEnabled(true);
            collapseTreeAction.setEnabled(true);
        }

        ActivityTracker.getInstance().inc();
    }

    @NotNull
    private JComponent createToolbarPanel()
    {
        DefaultActionGroup result = new DefaultActionGroup();

        result.add(findUsagesExcludingParentsAction);
        result.add(findUsagesIncludingParentsAction);
        result.add(stopFindUsagesAction);
        result.add(expandTreeAction);
        result.add(collapseTreeAction);

        return ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.STRUCTURE_VIEW_TOOLBAR, result, true)
            .getComponent();
    }

    public void createAndRenderTree(PsiMethodImpl element, boolean includeSupers)
    {
        setLoading(true);

        TreeGenerator treeGenerator = new TreeGenerator(this, project, element, includeSupers);

        progressIndicator = new BackgroundableProcessIndicator(treeGenerator);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(treeGenerator, progressIndicator);
    }

    public void finishCreatingTree(Tree tree)
    {
        this.tree = tree;
        treeView = new JBScrollPane(tree);
        setLoading(false);
    }

    public void stop()
    {
        tree = null;
        treeView.removeAll();
        forcedCancel = true;
        progressIndicator.cancel();
        setLoading(false);
    }

    public JPanel getContent()
    {
        return generalPanel;
    }
}