package ku.rsc;

/**
 * bug to fix:
 * 1. 修改配置, 加载配置 : 删除所有的所有的界面图形, 重新init, init的时候, 先要清除上一次的线程残留
 *         1. 修改的时候, 使用默认参数保证, 可以修改部分的数值
 *
 * 2. 实现新增加节点的功能
 *
 * 3. 检查 destroy 节点 和 revive 节点的
 * 4. 修改弹框的label 内容
 */
public class Transmute {
    public static void main(String[] args) {
        String a = "123";
        for (int i = 0; i < a.length(); i++) {
            System.out.println();
        }
    }
}
