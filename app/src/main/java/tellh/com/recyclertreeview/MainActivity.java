package tellh.com.recyclertreeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tellh.com.recyclertreeview.bean.Dir;
import tellh.com.recyclertreeview.bean.File;
import tellh.com.recyclertreeview.viewbinder.DirectoryNodeBinder;
import tellh.com.recyclertreeview.viewbinder.FileNodeBinder;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TreeViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        List<TreeNode> nodes = new ArrayList<>();
        TreeNode<Dir> app = new TreeNode<>(new Dir("app"));
        nodes.add(app);
        app.addChild(
                new TreeNode<Dir>(new Dir("manifests"))
                        .addChild(new TreeNode<File>(new File("AndroidManifest.xml")))
        );
        app.addChild(
                new TreeNode<>(new Dir("java")).addChild(
                        new TreeNode<>(new Dir("tellh")).addChild(
                                new TreeNode<>(new Dir("com")).addChild(
                                        new TreeNode<>(new Dir("recyclertreeview"))
                                                .addChild(new TreeNode<>(new File("Dir")))
                                                .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                                .addChild(new TreeNode<>(new File("File")))
                                                .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                                .addChild(new TreeNode<>(new File("TreeViewBinder")))
                                )
                        )
                )
        );
        TreeNode<Dir> res = new TreeNode<>(new Dir("res"));
        nodes.add(res);
        res.addChild(
                new TreeNode<>(new Dir("layout")).lock() // lock this TreeNode
                        .addChild(new TreeNode<>(new File("activity_main.xml")))
                        .addChild(new TreeNode<>(new File("item_dir.xml")))
                        .addChild(new TreeNode<>(new File("item_file.xml")))
        );
        res.addChild(
                new TreeNode<>(new Dir("mipmap"))
                        .addChild(new TreeNode<>(new File("ic_launcher.png")))
        );
        TreeNode<Dir> mgzx = new TreeNode<>(new Dir("美国专线"));
        mgzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        TreeNode<Dir> rbzx = new TreeNode<>(new Dir("日本专线"));
        rbzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        TreeNode<Dir> zgzx = new TreeNode<>(new Dir("中国专线"));
        zgzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        TreeNode<Dir> fzzx = new TreeNode<>(new Dir("非洲专线"));
        fzzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        TreeNode<Dir> ozzx = new TreeNode<>(new Dir("欧洲专线"));
        ozzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        TreeNode<Dir> bmzx = new TreeNode<>(new Dir("北美专线"));
        bmzx.addChild(new TreeNode<>(new Dir("java")).addChild(
                new TreeNode<>(new Dir("tellh")).addChild(
                        new TreeNode<>(new Dir("com")).addChild(
                                new TreeNode<>(new Dir("recyclertreeview"))
                                        .addChild(new TreeNode<>(new File("Dir")))
                                        .addChild(new TreeNode<>(new File("DirectoryNodeBinder")))
                                        .addChild(new TreeNode<>(new File("File")))
                                        .addChild(new TreeNode<>(new File("FileNodeBinder")))
                                        .addChild(new TreeNode<>(new File("TreeViewBinder")))
                        )
                )
        ));
        app.addChild(mgzx);
        app.addChild(rbzx);
        app.addChild(zgzx);
        app.addChild(fzzx);
        app.addChild(ozzx);
        app.addChild(bmzx);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeViewAdapter(nodes, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()));
        // whether collapse child nodes when their parent node was close.
//        adapter.ifCollapseChildWhileCollapseParent(true);
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) {
                    //Update and toggle the node.
                    onToggle(!node.isExpand(), holder);
//                    if (!node.isExpand())
//                        adapter.collapseBrotherNode(node);
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : -90;
                ivArrow.animate().rotationBy(rotateDegree)
                        .start();
            }
        });
        rv.setAdapter(adapter);
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.id_action_close_all:
                adapter.collapseAll();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
