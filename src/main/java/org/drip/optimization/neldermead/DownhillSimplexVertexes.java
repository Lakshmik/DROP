
package org.drip.optimization.neldermead;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.function.definition.RdToR1;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>DownhillSimplexVertexes</i> holds the Evolving the Ordered Vertexes employed in the Nelder-Mead Scheme.
 * 	The References are:
 *  
 * <br>
 * 	<ul>
 *  	<li>
 *  		Kolda, T. G., R. M., Lewis, and V. Torczon (2003): Optimization by Direct Search: New
 *  			Perspectives on some Classical and Modern Methods <i>SIAM Review</i> <b>45 (3)</b> 385-482
 *  	</li>
 *  	<li>
 *  		Lewis, R. M., A. Shepherd, and V. Torczon (2007): Implementing Generating Set Search Methods for
 *  			Linearly Constrained Minimization <i>SIAM Journal of Scientific Computing</i> <b>29 (6)</b>
 *  			2507-2530
 *  	</li>
 * 		<li>
 * 			Nash, J. C. (1979): <i>Compact Numerical Methods: Linear Algebra and Function Minimization</i>
 * 				<b>Rutledge</b> New York NY
 * 		</li>
 * 		<li>
 * 			Press, W. H., S. A. Teukolsky, W. T. Vetterling, and B. P. Flannery (2007): <i>Numerical Recipes
 * 				in C: The Art of Scientific Computing 3<sup>rd</sup> Edition</i> <b>Cambridge University
 * 				Press</b> New York NY
 * 		</li>
 * 		<li>
 * 			Wikipedia (2026): Nelder-Mead Method https://en.wikipedia.org/wiki/Nelder%E2%80%93Mead_method
 * 		</li>
 * 	</ul>
 *
 * <br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalOptimizerLibrary.md">Numerical Optimizer Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/optimization/README.md">Necessary, Sufficient, and Regularity Checks for Gradient Descent and LP/MILP/MINLP Schemes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/optimization/neldermead/README.md">Nelder-Mead R<sup>d</sup> Function Optimization</a></td></tr>
 *  </table>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DownhillSimplexVertexes
{
	private static final double ABSOLUTE_TOLERANCE = 1.e-07;

	private double[] _centroidVertex = null;
	private double _objectiveFunctionMean = Double.NaN;
	private double _objectiveFunctionStandardError = Double.NaN;
	private TreeMap<Double, List<double[]>> _orderedVertexListMap = null;

	/**
	 * Construct a Standard Instance of <i>DownhillSimplexVertexes</i>
	 * 
	 * @param vertexList List of Vertexes
	 * @param objectiveFunction Objective Function
	 * 
	 * @return Standard Instance of <i>DownhillSimplexVertexes</i>
	 */

	public static final DownhillSimplexVertexes Standard (
		final List<double[]> vertexList,
		final RdToR1 objectiveFunction)
	{
		if (null == vertexList || 0 == vertexList.size() || null == objectiveFunction) {
			return null;
		}

		TreeMap<Double, List<double[]>> orderedVertexListMap = new TreeMap<Double, List<double[]>>();

		try {
			for (int vertexIndex = 0; vertexIndex < vertexList.size(); ++vertexIndex) {
				double[] vertex = vertexList.get (vertexIndex);

				double value = objectiveFunction.evaluate (vertex);

				if (orderedVertexListMap.containsKey (value)) {
					orderedVertexListMap.get (value).add (vertex);
				} else {
					List<double[]> valueVertexList = new ArrayList<double[]>();

					vertexList.add (vertex);

					orderedVertexListMap.put (value, valueVertexList);
				}
			}

			DownhillSimplexVertexes downhillSimplexVertexes =
				new DownhillSimplexVertexes (orderedVertexListMap);

			return downhillSimplexVertexes.updateAggregateMetrics() ? downhillSimplexVertexes : null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private DownhillSimplexVertexes (
		final TreeMap<Double, List<double[]>> orderedVertexListMap)
		throws Exception
	{
		if (null == (_orderedVertexListMap = orderedVertexListMap) || 0 == _orderedVertexListMap.size()) {
			throw new Exception ("DownhillSimplexVertexes Constructor => Invalid Inputs");
		}
	}

	private boolean updateAggregateMetrics()
	{
		int vertexCount = 0;
		_centroidVertex = null;
		_objectiveFunctionMean = 0.;

		try {
			for (double valueKey : _orderedVertexListMap.keySet()) {
				for (double[] vertex : _orderedVertexListMap.get (valueKey)) {
					++vertexCount;
					_objectiveFunctionMean += valueKey;

					if (null == _centroidVertex) {
						_centroidVertex = new double[vertex.length];

						for (int vertexIndex = 0; vertexIndex < vertex.length; ++vertexIndex) {
							_centroidVertex[vertexIndex] = 0.;
						}
					}

					for (int vertexIndex = 0; vertexIndex < _centroidVertex.length; ++vertexIndex) {
						_centroidVertex[vertexIndex] += vertex[vertexIndex];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		for (int vertexIndex = 0; vertexIndex < _centroidVertex.length; ++vertexIndex) {
			_centroidVertex[vertexIndex] /= vertexCount;
		}

		_objectiveFunctionStandardError = 0.;
		_objectiveFunctionMean /= vertexCount;

		try {
			for (double valueKey : _orderedVertexListMap.keySet()) {
				double gap = _objectiveFunctionMean - valueKey;

				_objectiveFunctionStandardError += _orderedVertexListMap.get (valueKey).size() * gap * gap;
			}

			_objectiveFunctionStandardError = Math.sqrt (_objectiveFunctionStandardError / vertexCount);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Retrieve the Ordered Map of the Simplex Vertexes
	 * 
	 * @return Ordered Map of the Simplex Vertexes
	 */

	public TreeMap<Double, List<double[]>> orderedVertexListMap()
	{
		return _orderedVertexListMap;
	}

	/**
	 * Retrieve the Centroid Vertex
	 * 
	 * @return Centroid Vertex
	 */

	public double[] centroidVertex()
	{
		return _centroidVertex;
	}

	/**
	 * Retrieve the Objective Function Mean
	 * 
	 * @return Objective Function Mean
	 */

	public double objectiveFunctionMean()
	{
		return _objectiveFunctionMean;
	}

	/**
	 * Retrieve the Objective Function Standard Error
	 * 
	 * @return Objective Function Standard Error
	 */

	public double objectiveFunctionStandardError()
	{
		return _objectiveFunctionStandardError;
	}

	/**
	 * Swap In/Out Simplex Nodes
	 * 
	 * @param swapOutObjectiveFunctionValue Swap-Out Objective Function Value
	 * @param swapInObjectiveFunctionValue Swap-In Objective Function Value
	 * @param swapInVertex Swap-In Vertex
	 * 
	 * @return TRUE - Simplex Nodes successfully swapped In/Out
	 */

	public boolean swapNodes (
		final double swapOutObjectiveFunctionValue,
		final double swapInObjectiveFunctionValue,
		final double[] swapInVertex)
	{
		_orderedVertexListMap.remove (swapOutObjectiveFunctionValue);

		if (_orderedVertexListMap.containsKey (swapInObjectiveFunctionValue)) {
			_orderedVertexListMap.get (swapInObjectiveFunctionValue).add (swapInVertex);
		} else {
			List<double[]> vertexList = new ArrayList<double[]>();

			vertexList.add (swapInVertex);

			_orderedVertexListMap.put (swapInObjectiveFunctionValue, vertexList);
		}

		return updateAggregateMetrics();
	}

	/**
	 * Retrieve the Vertex with the Lowest Value
	 * 
	 * @return Vertex with the Lowest Value
	 */

	public double[] lowestValueVertex()
	{
		return _orderedVertexListMap.firstEntry().getValue().get (0);
	}

	/**
	 * Retrieve the Lowest Value
	 * 
	 * @return Lowest Value
	 */

	public double lowestValue()
	{
		return _orderedVertexListMap.firstEntry().getKey();
	}

	/**
	 * Retrieve the Vertex with the Highest Value
	 * 
	 * @return Vertex with the Highest Value
	 */

	public double[] highestValueVertex()
	{
		return _orderedVertexListMap.lastEntry().getValue().get (0);
	}

	/**
	 * Retrieve the Highest Value
	 * 
	 * @return Highest Value
	 */

	public double highestValue()
	{
		return _orderedVertexListMap.lastEntry().getKey();
	}

	/**
	 * Retrieve the Penultimate Highest Value
	 * 
	 * @return Penultimate Highest Value
	 */

	public double penultimateHighestValue()
	{
		return _orderedVertexListMap.lowerKey (_orderedVertexListMap.lastEntry().getKey());
	}

	/**
	 * Indicate if Convergence was Reached
	 * 
	 * @return TRUE - Convergence was Reached
	 */

	public boolean convergenceReached()
	{
		return ABSOLUTE_TOLERANCE >= _objectiveFunctionStandardError;
	}
}
