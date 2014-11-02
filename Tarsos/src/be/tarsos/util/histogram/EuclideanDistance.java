/*
*              _______                      
*             |__   __|                     
*                | | __ _ _ __ ___  ___  ___
*                | |/ _` | '__/ __|/ _ \/ __| 
*                | | (_| | |  \__ \ (_) \__ \    
*                |_|\__,_|_|  |___/\___/|___/    
*                                                         
* -----------------------------------------------------------
*
*  Tarsos is developed by Joren Six at 
*  The School of Arts,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
* -----------------------------------------------------------
*
*  Info: http://tarsos.0110.be
*  Github: https://github.com/JorenSix/Tarsos
*  Releases: http://tarsos.0110.be/releases/Tarsos/
*  
*  Tarsos includes some source code by various authors,
*  for credits and info, see README.
* 
*/

package be.tarsos.util.histogram;

/**
 * Euclidean Distance (L2 norm) for modulo type histograms.
 * 
 * @author Joren Six
 */
public final class EuclideanDistance implements HistogramCorrelation {

	public double correlation(final Histogram thisHistogam, final int displacement,
			final Histogram otherHistogram) {
		// number of bins (classes)
		final int numberOfClasses = thisHistogam.getNumberOfClasses();
		// start value
		final double start = thisHistogam.getStart();
		// stop value
		final double stop = thisHistogam.getStop();
		// classWidth
		final double classWidth = thisHistogam.getClassWidth();

		int actualDisplacement = displacement;
		// make displacement positive
		if (actualDisplacement < 0) {
			actualDisplacement = (actualDisplacement % numberOfClasses + numberOfClasses) % numberOfClasses;
		}

		double distance = 0.0;

		for (double current = start + classWidth / 2; current <= stop; current += classWidth) {
			final double displacedValue;
			displacedValue = (current + actualDisplacement * classWidth) % (numberOfClasses * classWidth);
			distance += Math.pow(thisHistogam.getCount(current) - otherHistogram.getCount(displacedValue), 2);
		}

		return -1 * Math.pow(distance, 0.5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * tarsos.util.histogram.HistogramCorrelation#plotCorrelation(tarsos.util
	 * .histogram.Histogram, int, tarsos.util.histogram.Histogram)
	 */
	public void plotCorrelation(final Histogram thisHistogram, final int displacement,
			final Histogram otherHistogram, final String fileName, final String title) {
		// for the moment this plots the intersection (not the euclidean
		// distance)
		new Intersection().plotCorrelation(thisHistogram, displacement, otherHistogram, fileName, title);
	}

}
