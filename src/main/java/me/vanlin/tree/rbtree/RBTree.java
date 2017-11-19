
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
        private K key;
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
        final Node<K, V> node = getNode(key); // 找到节点
        if (Objects.isNull(node)) {
            return null;
        }



        V value = node.value;
        deleteNode(node);
        return value;
    }

    /**
     * 删除节点
     * @param node
     */
    private V deleteNode(Node<K,V> node) {
        size--;

        // 被删除节点的左子树和右子树都不为空， 那么就用 node节点的中序后继节点代替node节点
        if (Objects.nonNull(node.leftNode) && Objects.nonNull(node.rightNode)) {
            Node<K, V> successor = successor(node);
            node.key = successor.key;
            node.value = successor.value;
            node = successor;
        }

        // 如果node的左子树存在 就用左子树替代  否则用右子树
        Node<K, V> replacement = (Objects.nonNull(node.leftNode) ? node.leftNode: node.rightNode);

        // 如果替代节点不为空
        if (Objects.nonNull(replacement)) {
            replacement.parentNode = node.parentNode;
            // 如果为根 则 replacement 为根
            if (Objects.isNull(node.parentNode)) {
                root = replacement;
                // 如果node为左节点 用 replacement 替代左节点
            } else if(node == node.parentNode.leftNode) {
                node.parentNode.leftNode = replacement;
            } else { // 用 replacement 替代 右节点
                node.parentNode.rightNode = replacement;
            }

            node.leftNode = node.rightNode = node.parentNode = null;

            if (node.color == BLACK) {
                rebalanceDeletion(replacement);
            }
        } else if (Objects.isNull(node.parentNode)) {
            root = null;
        } else {
            if (node.color == BLACK) {
                rebalanceDeletion(node);
            }

            if (Objects.nonNull(node.parentNode)) {
                if (node == node.parentNode.leftNode) {
                    node.parentNode.leftNode = null;
                } else if (node == node.parentNode.rightNode) {
                    node.parentNode.rightNode = null;
                }

                node.parentNode = null;
            }
        }

        return node.value;
    }

    /**
     * 后继结点
     * @param node
     * @return
     * @see http://www.importnew.com/19074.html
     */
    private Node<K, V> successor(Node<K,V> node) {
        if (node == null)
            return null;
        else if (node != null) {
            // 有右子树的节点，后继节点就是右子树的“最左节点”
            // 因为“最左子树”是右子树的最小节点
            Node<K,V> p = node.rightNode;
            while (p.leftNode != null)
                p = p.leftNode;
            return p;
        } else {
            // 如果右子树为空，则寻找当前节点所在左子树的第一个祖先节点
            // 因为左子树找完了，根据LDR该D了
            Node<K,V> p = node.parentNode;
            Node<K,V> ch = node;
            // 保证左子树
            while (p != null && ch == p.rightNode) {
                ch = p;
                p = p.parentNode;
            }
            return p;
        }
    }

    /**
     *
     * @param node
     * @see http://blog.csdn.net/hackbuteer1/article/details/7760584
     * @see http://blog.csdn.net/chenssy/article/details/26668941
     */
    private void rebalanceDeletion(Node<K, V> node) {
        // 删除节点需要一直迭代， 直到 node不是根节点 且 node的颜色是黑色
        while (node != root && colorOf(node) == BLACK) {
            if (node == leftOf(parentOf(node))) { // node是左节点
                Node<K, V> rightBrother = rightOf(parentOf(node));

                if (colorOf(rightBrother) == RED) { // 如果兄弟节点为红色   改变 W, P颜色 进行一次左旋
                    setColor(rightBrother, BLACK);
                    setColor(parentOf(node), RED);
                    rotateLeft(parentOf(node));
                    rightBrother = rightOf(parentOf(node));
                }

                if (colorOf(leftOf(rightBrother)) == BLACK && colorOf(rightOf(rightBrother)) == BLACK) {
                    setColor(rightBrother, RED);
                    node = parentOf(node);
                } else {
                    if (colorOf(rightOf(rightBrother)) == BLACK) {
                        setColor(leftOf(rightBrother), BLACK);
                        setColor(rightBrother, RED);
                        rotateRight(rightBrother);
                        rightBrother = rightOf(parentOf(node));
                    }

                    setColor(rightBrother, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(rightOf(rightBrother), BLACK);
                    rotateRight(parentOf(node));
                    node = root;
                }
            } else { // node是右节点
                Node<K, V> leftBrother = leftOf(parentOf(node));

                if (colorOf(leftBrother) == RED) {
                    setColor(leftBrother, BLACK);
                    setColor(parentOf(node), BLACK);
                    rotateRight(parentOf(node));
                    leftBrother = leftOf(parentOf(node));
                }

                if (colorOf(rightOf(leftBrother)) == BLACK && colorOf(leftOf(leftBrother)) == BLACK) {
                    setColor(leftBrother, RED);
                    node = parentOf(node);
                } else {
                    if (colorOf(leftOf(leftBrother)) == BLACK) {
                        setColor(rightOf(leftBrother), BLACK);
                        setColor(leftBrother, RED);
                        rotateLeft(leftBrother);
                        leftBrother = leftOf(parentOf(node));
                    }

                    setColor(leftBrother, colorOf(parentOf(node)));
                    setColor(parentOf(node), BLACK);
                    setColor(leftOf(leftBrother), BLACK);
                    rotateRight((parentOf(node)));
                    node = root;
                }
            }
        }
        setColor(node, BLACK);
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
