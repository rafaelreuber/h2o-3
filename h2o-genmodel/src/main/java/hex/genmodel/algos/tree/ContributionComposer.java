package hex.genmodel.algos.tree;

import hex.genmodel.utils.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;

public class ContributionComposer implements Serializable {
    
    /**
     * Sort shapley values and compose desired output
     *
     * @param contribs Raw contributions to be composed
     * @param contribNameIds Contribution corresponding feature ids
     * @param topN Return only #topN highest contributions + bias.
     * @param topBottomN Return only #topBottomN lowest contributions + bias
     *                   If topN and topBottomN are defined together then return array of #topN + #topBottomN + bias
     * @param abs True to compare absolute values of contributions
     * @return Sorted KeyValue array of contributions of size #topN + #topBottomN + bias
     *         If topN < 0 || topBottomN < 0 then all descending sorted contributions is returned.
     */
    public final int[] composeContributions(final int[] contribNameIds, final float[] contribs, int topN, int topBottomN, boolean abs) {
        if (topBottomN == 0) {
            return composeSortedContributions(contribNameIds, contribs, topN, abs, -1);
        } else if (topN == 0) {
            return composeSortedContributions(contribNameIds, contribs, topBottomN, abs,1);
        } else if ((topN + topBottomN) >= contribs.length || topN < 0 || topBottomN < 0) {
            return composeSortedContributions(contribNameIds, contribs, contribs.length, abs, -1);
        }

        composeSortedContributions(contribNameIds, contribs, contribNameIds.length, abs,-1);
        int[] bottomSorted = Arrays.copyOfRange(contribNameIds, contribNameIds.length - 1 - topBottomN, contribNameIds.length);
        reverse(bottomSorted, contribs, bottomSorted.length - 1);
        int[] contribNameIdsTmp = Arrays.copyOf(contribNameIds, topN);

        return ArrayUtils.append(contribNameIdsTmp, bottomSorted);
    }
    
    public int checkAndAdjustInput(int n, int len) {
        if (n < 0 || n > len) {
            return len;
        }
        return n;
    }
    
    private int[] composeSortedContributions(final int[] contribNameIds, final float[] contribs, int n, boolean abs, int increasing) {
        int nAdjusted = checkAndAdjustInput(n, contribs.length);
        sortContributions(contribNameIds, contribs, abs, increasing);
        if (nAdjusted < contribs.length) {
            int bias = contribNameIds[contribs.length-1];
            int[] contribNameIdsSorted = Arrays.copyOfRange(contribNameIds, 0, nAdjusted + 1);
            contribNameIdsSorted[nAdjusted] = bias;
            return contribNameIdsSorted;
        }
        return contribNameIds;
    }
    
    private void sortContributions(final int[] contribNameIds, final float[] contribs, final boolean abs, final int increasing) {
        ArrayUtils.sort(contribNameIds, contribs, 0, contribs.length -1, abs, increasing);
    }

    private void reverse(int[] contribNameIds, float[] contribs, int len) {
        for (int i = 0; i < len/2; i++) {
            if (contribs[contribNameIds[i]] != contribs[contribNameIds[len - i - 1]]) {
                int tmp = contribNameIds[i];
                contribNameIds[i] = contribNameIds[len - i - 1];
                contribNameIds[len - i - 1] = tmp;
            }
        }
    }
}
