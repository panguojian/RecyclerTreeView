package tellh.com.recyclertreeview_lib;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pgj on 2021/8/10
 **/
public class NTreeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String KEY_IS_EXPAND = "IS_EXPAND";
    private final List<? extends TreeViewBinder> viewBinders;
    private List<TreeNode> displayNodes;
    private int padding = 30;
    private OnTreeNodeListener onTreeNodeListener;
    private boolean toCollapseChild;

    public NTreeViewAdapter(List<? extends TreeViewBinder> viewBinders) {
        this(null, viewBinders);
    }

    public NTreeViewAdapter(@Nullable List<TreeNode> nodes, List<? extends TreeViewBinder> viewBinders) {
        displayNodes = new ArrayList<>();
        if (nodes != null) {
            findDisplayNodes(nodes);
        }
        this.viewBinders = viewBinders;
    }

    private void findDisplayNodes(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            displayNodes.add(node);
            if (!node.isLeaf() && node.isExpand()) {
                findDisplayNodes(node.getChildList());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return displayNodes.get(position).getContent().getLayoutId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewBinders.size() == 1) {
            return viewBinders.get(0).provideViewHolder(v);
        }
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == viewType) {
                return viewBinder.provideViewHolder(v);
            }
        }
        return viewBinders.get(0).provideViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            Bundle b = (Bundle) payloads.get(0);
            for (String key : b.keySet()) {
                switch (key) {
                    case KEY_IS_EXPAND:
                        if (onTreeNodeListener != null)
                            onTreeNodeListener.onToggle(b.getBoolean(key), holder);
                        break;
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.itemView.setPaddingRelative(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        } else {
            holder.itemView.setPadding(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
                try {
                    long lastClickTime = (long) holder.itemView.getTag();
                    if (System.currentTimeMillis() - lastClickTime < 500)
                        return;
                } catch (Exception e) {
                    holder.itemView.setTag(System.currentTimeMillis());
                }
                holder.itemView.setTag(System.currentTimeMillis());

                /**********************************************************************/
                if (onTreeNodeListener != null && onTreeNodeListener.onClick(selectedNode, holder))
                    return;
                if (selectedNode.isLeaf())
                    return;
                //This TreeNode was locked to click.
                if (selectedNode.isLocked()) return;
                boolean isExpand = selectedNode.isExpand();
                int positionStart = displayNodes.indexOf(selectedNode) + 1;
                if (!isExpand) {
                    notifyItemRangeInserted(positionStart, addChildNodes(selectedNode, positionStart));
                } else {
                    notifyItemRangeRemoved(positionStart, removeChildNodes(selectedNode, true));
                }
            }
        });
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == displayNodes.get(position).getContent().getLayoutId())
                viewBinder.bindView(holder, position, displayNodes.get(position));
        }
    }

    private int addChildNodes(TreeNode pNode, int startIndex) {
        List<TreeNode> childList = pNode.getChildList();
        int addChildCount = 0;
        for (TreeNode treeNode : childList) {
            displayNodes.add(startIndex + addChildCount++, treeNode);
            if (treeNode.isExpand()) {
                addChildCount += addChildNodes(treeNode, startIndex + addChildCount);
            }
        }
        if (!pNode.isExpand())
            pNode.toggle();
        return addChildCount;
    }

    private int removeChildNodes(TreeNode pNode) {
        return removeChildNodes(pNode, true);
    }

    private int removeChildNodes(TreeNode pNode, boolean shouldToggle) {
        if (pNode.isLeaf()) {
            return 0;
        }
        List<TreeNode> childList = pNode.getChildList();
        int removeChildCount = childList.size();
        displayNodes.removeAll(childList);
        for (TreeNode child : childList) {
            if (child.isExpand()) {
                /**
                 * ??
                 */
                if (toCollapseChild)
                    child.toggle();
                removeChildCount += removeChildNodes(child, false);
            }
        }
        /**
         * ???
         */
        if (shouldToggle)
            pNode.toggle();
        return removeChildCount;
    }

    @Override
    public int getItemCount() {
        return displayNodes == null ? 0 : displayNodes.size();
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    /**
     * 关闭孩子列表的同时是否关闭父列表
     *
     * @param toCollapseChild
     */
    public void ifCollapseChildWhileCollapseParent(boolean toCollapseChild) {
        this.toCollapseChild = toCollapseChild;
    }

    public void setOnTreeNodeListener(OnTreeNodeListener onTreeNodeListener) {
        this.onTreeNodeListener = onTreeNodeListener;
    }

    public interface OnTreeNodeListener {
        /**
         * called when TreeNodes were clicked.
         *
         * @return weather consume the click event.
         */
        boolean onClick(TreeNode node, RecyclerView.ViewHolder holder);

        /**
         * called when TreeNodes were toggle.
         *
         * @param isExpand the status of TreeNodes after being toggled.
         */
        void onToggle(boolean isExpand, RecyclerView.ViewHolder holder);
    }

    public void refresh(List<TreeNode> treeNodes) {
        displayNodes.clear();
        findDisplayNodes(treeNodes);
        /**
         * 对比这几个函数的区别是啥？
         */
        notifyDataSetChanged();
    }

    public Iterator<TreeNode> getDisplayNodesIterator() {
        return displayNodes.iterator();
    }

    //TODO DiffUtil这玩意有啥用？?
    private void notifyDiff(List<TreeNode> temp) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return temp.size();
            }

            @Override
            public int getNewListSize() {
                return displayNodes.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return NTreeViewAdapter.this.areItemTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return NTreeViewAdapter.this.areContentsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return NTreeViewAdapter.this.getChangePayload(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    private Object getChangePayload(TreeNode oldNode, TreeNode newNode) {
        Bundle diffBundle = new Bundle();
        if (newNode.isExpand() != oldNode.isExpand()) {
            diffBundle.putBoolean(KEY_IS_EXPAND, newNode.isExpand());
        }
        if (diffBundle.size() == 0)
            return null;
        return diffBundle;
    }

    // For DiffUtil, if they are the same items, whether the contents has bean changed.
    private boolean areContentsTheSame(TreeNode oldNode, TreeNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent())
                && oldNode.isExpand() == newNode.isExpand();
    }

    // judge if the same item for DiffUtil
    private boolean areItemTheSame(TreeNode oldNode, TreeNode newNode) {
        return oldNode.getContent() != null && oldNode.getContent().equals(newNode.getContent());
    }

    /**
     * collapse all root nodes.
     */
    public void collapseAll() {
        // Back up the nodes are displaying.
        List<TreeNode> temp = backupDisplayNodes();
        //find all root nodes.
        List<TreeNode> roots = new ArrayList<>();
        for (TreeNode displayNode : displayNodes) {
            if (displayNode.isRoot())
                roots.add(displayNode);
        }
        //Close all root nodes.
        for (TreeNode root : roots) {
            if (root.isExpand())
                removeChildNodes(root);
        }
        notifyDiff(temp);
    }


    /**
     * 为什么要备份？？
     *
     * @return
     */
    @NonNull
    private List<TreeNode> backupDisplayNodes() {
        List<TreeNode> temp = new ArrayList<>();
        for (TreeNode displayNode : displayNodes) {
            try {
                temp.add(displayNode.clone());
            } catch (CloneNotSupportedException e) {
                temp.add(displayNode);
            }
        }
        return temp;
    }

    public void collapseNode(TreeNode pNode) {
        List<TreeNode> temp = backupDisplayNodes();
        removeChildNodes(pNode);
        notifyDiff(temp);
    }

    /**
     * 关闭兄弟节点
     *
     * @param pNode
     */
    public void collapseBrotherNode(TreeNode pNode) {
        List<TreeNode> temp = backupDisplayNodes();
        if (pNode.isRoot()) {
            List<TreeNode> roots = new ArrayList<>();
            for (TreeNode displayNode : displayNodes) {
                if (displayNode.isRoot())
                    roots.add(displayNode);
            }
            //Close all root nodes.
            for (TreeNode root : roots) {
                if (root.isExpand() && !root.equals(pNode))
                    removeChildNodes(root);
            }
        } else {
            TreeNode parent = pNode.getParent();
            if (parent == null) {
                return;
            }
            List<TreeNode> childList = parent.getChildList();
            for (TreeNode node : childList) {
                if (node.equals(pNode) || !node.isExpand())
                    continue;
                removeChildNodes(node);
            }
        }
        notifyDiff(temp);
    }
}
