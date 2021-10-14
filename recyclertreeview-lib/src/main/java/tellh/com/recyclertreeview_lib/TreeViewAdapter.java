package tellh.com.recyclertreeview_lib;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tlh on 2016/10/1 :)
 */
public class TreeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String KEY_IS_EXPAND = "IS_EXPAND";
    private final List<? extends TreeViewBinder> viewBinders;
    private List<TreeNode> displayNodes;
    private int padding = 30;
    private OnTreeNodeListener onTreeNodeListener;
    //
    private boolean toCollapseChild;

    public TreeViewAdapter(List<? extends TreeViewBinder> viewBinders) {
        this(null, viewBinders);
    }

    public TreeViewAdapter(@Nullable List<TreeNode> nodes, List<? extends TreeViewBinder> viewBinders) {
        displayNodes = new ArrayList<>();
        if (nodes != null)
            findDisplayNodes(nodes);
        this.viewBinders = viewBinders;
    }

    /**
     * 从nodes的结点中寻找展开了的非叶结点，添加到displayNodes中。
     * 实际上最外层的都会添加进来
     * @param nodes 基准点
     */
    private void findDisplayNodes(List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            displayNodes.add(node);
            if (!node.isLeaf() && node.isExpand())
                //递归算法
                findDisplayNodes(node.getChildList());
        }
    }

    /**
     * 根据不同节点展示不同的布局文件
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return displayNodes.get(position).getContent().getLayoutId();
    }

    /**
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        if (viewBinders.size() == 1)
            return viewBinders.get(0).provideViewHolder(v);
        for (TreeViewBinder viewBinder : viewBinders) {
            if (viewBinder.getLayoutId() == viewType)
                return viewBinder.provideViewHolder(v);
        }
        return viewBinders.get(0).provideViewHolder(v);
    }

    /**
     *
     * @param holder
     * @param position
     * @param payloads
     */
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        /**
         * 这是干啥用的
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            holder.itemView.setPaddingRelative(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        }else {
            holder.itemView.setPadding(displayNodes.get(position).getHeight() * padding, 3, 3, 3);
        }

        /**
         *
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //? ?
                TreeNode selectedNode = displayNodes.get(holder.getLayoutPosition());
                // Prevent multi-click during the short interval.
                try {
                    long lastClickTime = (long) holder.itemView.getTag();
                    if (System.currentTimeMillis() - lastClickTime < 500)
                        return;
                } catch (Exception e) {
                    holder.itemView.setTag(System.currentTimeMillis());
                }
                holder.itemView.setTag(System.currentTimeMillis());

                if (onTreeNodeListener != null && onTreeNodeListener.onClick(selectedNode, holder))
                    return;
                if (selectedNode.isLeaf())
                    return;
                // This TreeNode was locked to click.
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
        if (pNode.isLeaf())
            return 0;
        List<TreeNode> childList = pNode.getChildList();
        int removeChildCount = childList.size();
        displayNodes.removeAll(childList);
        for (TreeNode child : childList) {
            /**
             * 防止下次加进来的时候，还存在子列表？？
             */
            if (child.isExpand()) {
                if (toCollapseChild)
                    child.toggle();
                removeChildCount += removeChildNodes(child, false);
            }
        }
        /**
         * 不明白这里是干嘛用的
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

    public void ifCollapseChildWhileCollapseParent(boolean toCollapseChild) {
        this.toCollapseChild = toCollapseChild;
    }

    public void setOnTreeNodeListener(OnTreeNodeListener onTreeNodeListener) {
        this.onTreeNodeListener = onTreeNodeListener;
    }

    public interface OnTreeNodeListener {
        /**
         * called when TreeNodes were clicked.
         * @return weather consume the click event.
         */
        boolean onClick(TreeNode node, RecyclerView.ViewHolder holder);

        /**
         * called when TreeNodes were toggle.
         * @param isExpand the status of TreeNodes after being toggled.
         */
        void onToggle(boolean isExpand, RecyclerView.ViewHolder holder);
    }

    public void refresh(List<TreeNode> treeNodes) {
        displayNodes.clear();
        findDisplayNodes(treeNodes);
        notifyDataSetChanged();
    }

    public Iterator<TreeNode> getDisplayNodesIterator() {
        return displayNodes.iterator();
    }

    private void notifyDiff(final List<TreeNode> temp) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            //老数据集size
            @Override
            public int getOldListSize() {
                return temp.size();
            }

            //新数据集size
            @Override
            public int getNewListSize() {
                return displayNodes.size();
            }

            /**
             * 新老数据集在同一个postion的Item是否是一个对象？（
             * 可能内容不同，如果这里返回true，会调用下面的方法）
             *
             * 被DiffUtil调用，用来判断 两个对象是否是相同的Item。
             * 例如，如果你的Item有唯一的id字段，这个方法就 判断id是否相等。
             * 本例判断name字段是否一致
             */
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.areItemsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            /**
             * 这个方法仅仅是上面方法返回ture才会调用，
             * 我的理解是只有notifyItemRangeChanged()才会调用，
             * 判断item的内容是否有变化
             *
             * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
             * DiffUtil用返回的信息（true false）来检测当前item的内容是否发生了变化
             * DiffUtil 用这个方法替代equals方法去检查是否相等。
             * 所以你可以根据你的UI去改变它的返回值
             * 例如，如果你用RecyclerView.Adapter 配合DiffUtil使用，你需要返回Item的视觉表现是否相同。
             * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
             */
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.areContentsTheSame(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
            }

            //该方法在DiffUtil高级用法中用到 ，暂且不提
            @Nullable
            @Override
            public Object getChangePayload(int oldItemPosition, int newItemPosition) {
                return TreeViewAdapter.this.getChangePayload(temp.get(oldItemPosition), displayNodes.get(newItemPosition));
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
    private boolean areItemsTheSame(TreeNode oldNode, TreeNode newNode) {
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
            if (parent == null)
                return;
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
