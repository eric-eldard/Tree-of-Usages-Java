package treeOfUsages.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import treeOfUsages.Plugin;

public class CollapseTreeAction extends EnableableAction
{
    public Plugin plugin;

    @SuppressWarnings("unused")
    public CollapseTreeAction()
    {
    }

    public CollapseTreeAction(Plugin plugin)
    {
        super(
            "Collapse All",
            "Collapse all nodes in the current tree",
            AllIcons.Actions.Collapseall
        );
        this.plugin = plugin;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        plugin.collapseAll();
    }
}