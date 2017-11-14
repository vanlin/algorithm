
package me.vanlin.tree.rbtree;

import java.util.Objects;

/**
 * 红黑树
 */
final public class RBTree<K, V> {
    private Node<K, V> root;
    private transient int size = 0;

    private static final boolean RED   = false;
    private static final boolean BLACK = true;

    static class Node<K, V> implements Comparable<K> {
        final private K key;
        private V value;
        boolean color = BLACK;  //默认黑色

        Node parentNode;
        Node leftNode;
        Node rightNode;

        public Node(K key, V value, Node parentNode) {
            this.key = key;
            this.value = value;
            this.parentNode = parentNode;
        }

        public int compareTo(final K o) {
            return 0;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(final V value) {
            this.value = value;
        }


    }

    /**
     * 获取Node
     * @param key
     * @return
     */
    private Node<K, V> getNode(K key) {
        if (Objects.isNull(key)) {
            throw new NullPointerException("The key can't be null");
        }
        final Comparable<? super K> k = (Comparable<? super K>)key;

        Node<K, V> node = root; // 从根节点开始查找
        while (Objects.nonNull(node)) {
            int cmp = k.compareTo(node.key); // 与当前节点进行比较
            if (cmp < 0) { // 比当前节点小 则继续 比较左节点
                node = node.leftNode;
            } else if (cmp > 0) { // 比当前节点大 则继续比较右节点
                node = node.rightNode;
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * 获取值
     * @param key
     * @return
     */
    public V get(K key) {
        final Node<K, V> node = getNode(key);
        if (Objects.nonNull(node)) {
            return node.value;
        } else {
            return null;
        }
    }

    /**
     * 添加值
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value) {
        if (Objects.isNull(key)) {
            throw new NullPointerException("The key can't be null");
        }
        Node<K, V> temp = root;
        if (Objects.isNull(temp)) {
            root = new Node<>(key, value, null);
            size = 1;
            return value;
        }
        int cmp;
        Node<K, V> parent;
        Comparable<? super K> k = (Comparable<? super K>) key;//使用元素的自然顺序

        do {
            parent = temp;
            cmp = k.compareTo(temp.key); // 与当前节点比较
            if (cmp < 0) { // 比当前节点小 则继续比较 左节点
                temp = temp.leftNode;
            } else if (cmp > 0) { // 比当前节点大 则继续比较 右节点
                temp = temp.rightNode;
            } else { // 如果相等  替换值
                temp.setValue(value);
                return value;
            }
        } while (Objects.nonNull(temp)); // 直到temp为空

        final Node<K, V> node = new Node<>(key, value, parent);
        if (cmp < 0) {
            parent.leftNode = node;
        } else {
            parent.rightNode = node;
        }
        rebalanceInsertion(node);
        size++;
        return value;
    }

    public V remove(K key) {
        Node<K, V> node = getNode(key);
        if (Objects.isNull(node)) {
            return null;
        }

        V value = node.value;
        deleteNode(key);
        return value;
    }

    /**
     * 删除节点
     * @param key
     */
    private void deleteNode(K key) {

    }

    /**
     *
     * @param node
     * @see http://blog.csdn.net/hackbuteer1/article/details/7760584
     */
    private void rebalanceDeletion(Node<K, V> node) {

    }

    /**
     *
     * @param node
     * @see http://blog.csdn.net/hackbuteer1/article/details/7740956
     */
    private void rebalanceInsertion(Node<K, V> node) {
        node.color = RED;
        while (node != null && node != root && parentOf(node).color == RED) {
            if (parentOf(node) == leftOf(parentOf(parentOf(node)))) { // 如果父节点在祖父节点 的 左节点上
                Node<K, V> rightUncleNode = rightOf(parentOf(parentOf(node))); // 取祖父节点右节点(叔节点)
                if (colorOf(rightUncleNode) == RED) { //  红叔在右  更换颜色 向上迭代
                    setColor(parentOf(node), BLACK); // 置父节点为黑色
                    setColor(rightUncleNode, BLACK);
                    setColor(parentOf(parentOf(node)), RED); // 祖父节点置红色
                    node = parentOf(parentOf(node)); // 开始处理祖父节点
                } else { // 右边是黑叔
                    if (node == rightOf(parentOf(node))) { // 如果在右节点上 需要针对 父节点左旋
                        node = parentOf(node);
                        rotateLeft(node);
                    }
                    setColor(parentOf(node), BLACK); // 将左旋节点置黑
                    setColor(parentOf(parentOf(node)), RED);
                    rotateRight(parentOf(parentOf(node))); // 将祖父节点右旋
                }
            } else { // 父节点中祖父节点 右节点上
                Node<K, V> leftUncleNode = leftOf(parentOf(parentOf(node))); // 取祖父节点左节点 （叔节点）
                if (colorOf(leftUncleNode) == RED) { //  红叔在左  更换颜色 向上迭代
                    setColor(parentOf(node), BLACK);
                    setColor(leftUncleNode, BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    node = parentOf(parentOf(node));
                } else {// 左边是黑叔
                    if (node == leftOf(parentOf(node))) {
                        node = parentOf(node);
                        rotateRight(node);
                    }
                    setColor(parentOf(node), BLACK);
                    setColor(parentOf(parentOf(node)), RED);
                    rotateLeft(parentOf(parentOf(node)));
                }
            }
        }
        root.color = BLACK;
    }

    private Node<K, V> parentOf(final Node<K, V> node) {
        return node.parentNode;
    }
    private Node<K, V> leftOf(final Node<K, V> node) {
        return node.leftNode;
    }
    private Node<K, V> rightOf(final Node<K, V> node) {
        return node.rightNode;
    }
    private void setColor(final Node<K, V> node, final boolean color) {
        node.color = color;
    }
    private boolean colorOf(final Node<K, V> node) {
        if (Objects.isNull(node)) {
            return BLACK;
        }
        return node.color;
    }
    /**
     * 左旋
     * @param node
     */
    private void rotateLeft(final Node<K, V> node) {
        if (Objects.nonNull(node)) {
            final Node<K, V> rightNode = node.rightNode; // 拿到 当前节点 右子节点
            node.rightNode = rightNode.leftNode;
            if (Objects.nonNull(rightNode.leftNode)) { // 将右子节点 的左子节点 放到 当前节点的右子节点
                rightNode.leftNode.parentNode = node;
            }
            rightNode.parentNode = node.parentNode; // 右子节点取代 当前节点
            if (Objects.isNull(node.parentNode)) { // 如果没有父节点 右子节点为根节点
                root = rightNode;
            } else if (node.parentNode.leftNode == node) { // 交换父节点 左子节点
                node.parentNode.leftNode = rightNode;
            } else { // 交换父节点 右子节点
                node.parentNode.rightNode = rightNode;
            }

            rightNode.leftNode = node; // 当前节点右子节点 的 左子节点为当前节点
            node.parentNode = rightNode; // 当前节点父节点为 当前节点右子节点
        }
    }

    /**
     * 右旋
     * @param node
     */
    private void rotateRight(final Node<K, V> node) {
        if (Objects.nonNull(node)) {
            final Node<K, V> leftNode = node.leftNode;  // 拿到当前节点 左子节点
            node.leftNode = leftNode.rightNode;
            if (Objects.nonNull(leftNode.rightNode)) {
                leftNode.rightNode.parentNode = node;
            }
            leftNode.parentNode = node.parentNode;
            if (Objects.isNull(node.parentNode)) { // 如果当前节点没有根节点 新的根节点为 leftNode
                root = leftNode;
            } else if (node.parentNode.rightNode == node) {  // 交换父节点 右子节点
                node.parentNode.rightNode = leftNode;
            } else { // 交换父节点 左子节点
                node.parentNode.leftNode = leftNode;
            }
            leftNode.rightNode = node;
            node.parentNode = leftNode;
        }
    }

}
