import java.util.Random;

public class Measurements {

	public static void main(String[] args) {
        Random gen = new Random(2);

		for (int i = 1; i < 11; i++) {
			WAVLTree tree = new WAVLTree();
			int insertOps = 0;
			int deleteOps = 0;
			int maxInsertOps = 0;
			int maxDeleteOps = 0;
            for (int j = 0; j < i*10000; j++) {
            	int val = gen.nextInt();
            	int numInsert = tree.insert(val, "val");
            	if (numInsert==-1) {            		
                	val = gen.nextInt();
                	numInsert = tree.insert(val, "val");
            	}
            	insertOps+= numInsert;
            	maxInsertOps = Math.max(maxInsertOps, numInsert);
			}
            System.out.println(i*10000);
            System.out.println(tree.height(tree.root));
            System.out.println(tree.root.getSubtreeSize());

            for (int j = 0; j < i*10000; j++) {
            	int numDelete = tree.delete(tree.minNode().getKey());
            	deleteOps += numDelete;
            	maxDeleteOps = Math.max(maxDeleteOps, numDelete);
			}
            System.out.println("i = " + i+ " insert operations: "+ insertOps/(i*10000.0) + " max " + maxInsertOps);
            System.out.println("i = " + i+ " delete operations: "+ deleteOps/(i*10000.0)+ " max " + maxDeleteOps);
            
		}

	}

}
