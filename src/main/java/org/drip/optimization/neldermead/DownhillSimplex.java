
package org.drip.optimization.neldermead;

import java.util.ArrayList;
import java.util.List;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1.MultidimensionalRosenbrockCoupled;
import org.drip.function.rdtor1.Rosenbrock;
import org.drip.numerical.common.NumberUtil;

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
 * <i>DownhillSimplex</i> implements the the Canonical Nelder-Mead Scheme for Optimizing a R<sup>d</sup>
 *  Function. The References are:
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

public class DownhillSimplex
{
	private boolean _diagnosticsOn = false;
	private RdToR1 _objectiveFunction = null;
	private List<double[]> _vertexList = null;
	private AmoebaCoefficients _amoebaCoefficients = null;

	/**
	 * Construct a Standard Instance of <i>DownhillSimplex</i>
	 * 
	 * @param objectiveFunction Objective Function
	 * @param vertexList List of Vertexes
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @return Standard Instance of <i>DownhillSimplex</i>
	 */

	public static final DownhillSimplex Standard (
		final RdToR1 objectiveFunction,
		final List<double[]> vertexList,
		final boolean diagnosticsOn)
	{
		try {
			return new DownhillSimplex (
				objectiveFunction,
				vertexList,
				AmoebaCoefficients.Standard(),
				diagnosticsOn
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private double[] reflectedVertex (
		final double[] highestValueVertex,
		final double[] centroidVertex)
	{
		double reflectionCoefficient = _amoebaCoefficients.reflection();

		double[] reflectedVertex = new double[highestValueVertex.length];

		for (int vertexIndex = 0; vertexIndex < reflectedVertex.length; ++vertexIndex) {
			reflectedVertex[vertexIndex] = centroidVertex[vertexIndex] +
				reflectionCoefficient * (centroidVertex[vertexIndex] - highestValueVertex[vertexIndex]);
		}

		return reflectedVertex;
	}

	private double[] expandedVertex (
		final double[] reflectedVertex,
		final double[] centroidVertex)
	{
		double expansionCoefficient = _amoebaCoefficients.expansion();

		double[] expansionVertex = new double[centroidVertex.length];

		for (int vertexIndex = 0; vertexIndex < reflectedVertex.length; ++vertexIndex) {
			expansionVertex[vertexIndex] = centroidVertex[vertexIndex] +
				expansionCoefficient * (reflectedVertex[vertexIndex] - centroidVertex[vertexIndex]);
		}

		return expansionVertex;
	}

	private double[] contractedVertex (
		final double[] reflectedVertex,
		final double[] centroidVertex)
	{
		double contractionCoefficient = _amoebaCoefficients.contraction();

		double[] contractionVertex = new double[reflectedVertex.length];

		for (int vertexIndex = 0; vertexIndex < centroidVertex.length; ++vertexIndex) {
			contractionVertex[vertexIndex] = centroidVertex[vertexIndex] +
				contractionCoefficient * (reflectedVertex[vertexIndex] - centroidVertex[vertexIndex]);
		}

		return contractionVertex;
	}

	private DownhillSimplexVertexes shrinkVertexes (
		final double[] lowestValueVertex)
	{
		double shrinkCoefficient = _amoebaCoefficients.shrink();

		List<double[]> shrunkVertexList = new ArrayList<double[]>();

		for (double[] vertex : _vertexList) {
			double[] shrunkVertex = new double[vertex.length];

			for (int variateIndex = 0; variateIndex < vertex.length; ++variateIndex) {
				shrunkVertex[variateIndex] = lowestValueVertex[variateIndex] +
					shrinkCoefficient * (lowestValueVertex[variateIndex] - shrunkVertex[variateIndex]);
			}

			shrunkVertexList.add (shrunkVertex);
		}

		return DownhillSimplexVertexes.Standard (shrunkVertexList, _objectiveFunction);
	}

	/**
	 * <i>DownhillSimplex</i> Constructor
	 * 
	 * @param objectiveFunction Objective Function
	 * @param vertexList List of Vertexes
	 * @param amoebaCoefficients Nelder-Mead Control (i.e., Ameoba) Coefficients
	 * @param diagnosticsOn TRUE - Diagnostics has been Turned On
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public DownhillSimplex (
		final RdToR1 objectiveFunction,
		final List<double[]> vertexList,
		final AmoebaCoefficients amoebaCoefficients,
		final boolean diagnosticsOn)
		throws Exception
	{
		if (null == (_objectiveFunction = objectiveFunction) ||
			null == (_vertexList = vertexList) || 0 == _vertexList.size() ||
			null == (_amoebaCoefficients = amoebaCoefficients))
		{
			throw new Exception ("DownhillSimplex Constructor => Invalid Inputs");
		}

		int variateDimension = -1;
		_diagnosticsOn = diagnosticsOn;

		for (double[] vertex : _vertexList) {
			if (null == vertex) {
				throw new Exception ("DownhillSimplex Constructor => Invalid Inputs");
			}

			if (-1 == variateDimension) {
				variateDimension = vertex.length;
			} else {
				if (variateDimension != vertex.length) {
					throw new Exception ("DownhillSimplex Constructor => Invalid Inputs");
				}
			}
		}

		if (variateDimension != _objectiveFunction.dimension()) {
			throw new Exception ("DownhillSimplex Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Objective Function
	 * 
	 * @return Objective Function
	 */

	public RdToR1 objectiveFunction()
	{
		return _objectiveFunction;
	}

	/**
	 * Retrieve the List of Vertexes
	 * 
	 * @return List of Vertexes
	 */

	public List<double[]> vertexList()
	{
		return _vertexList;
	}

	/**
	 * Retrieve the Nelder-Mead Control (i.e., Ameoba) Coefficients
	 * 
	 * @return Nelder-Mead Control (i.e., Ameoba) Coefficients
	 */

	public AmoebaCoefficients amoebaCoefficients()
	{
		return _amoebaCoefficients;
	}

	/**
	 * Indicate if Diagnostics has been Turned On
	 * 
	 * @return TRUE - Diagnostics has been Turned On
	 */

	public boolean diagnosticsOn()
	{
		return _diagnosticsOn;
	}

	/**
	 * Run the Nelder-Mead Optimization
	 * 
	 * @return Results of the Nelder-Mead Optimization
	 */

	public DownhillSimplexRun controlRun()
	{
		DownhillSimplexVertexes vertexes =
			DownhillSimplexVertexes.Standard (_vertexList, _objectiveFunction);

		if (null == vertexes) {
			return null;
		}

		int iterationIndex = 0;
		double[] reflectedVertex = null;

		DownhillSimplexRun run = _diagnosticsOn ?
			new DownhillSimplexRunDiagnostics() : new DownhillSimplexRun();

		while (!vertexes.convergenceReached()) {
			double[] centroidVertex = vertexes.centroidVertex();

			if (run instanceof DownhillSimplexRunDiagnostics) {
				((DownhillSimplexRunDiagnostics) run).setCentroidVertex (iterationIndex, centroidVertex);
			}

			double[] highestValueVertex = vertexes.highestValueVertex();

			double highestValue = vertexes.highestValue();

			if (run instanceof DownhillSimplexRunDiagnostics) {
				try {
					((DownhillSimplexRunDiagnostics) run).setHighestObjectiveFunctionCoordinate (
						iterationIndex,
						new ObjectiveFunctionCoordinate (highestValueVertex, highestValue)
					);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (null == (reflectedVertex = reflectedVertex (highestValueVertex, centroidVertex))) {
				return null;
			}

			double reflectedValue = Double.NaN;

			try {
				reflectedValue = _objectiveFunction.evaluate (reflectedVertex);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			if (run instanceof DownhillSimplexRunDiagnostics) {
				try {
					((DownhillSimplexRunDiagnostics) run).setReflectedObjectiveFunctionCoordinate (
						iterationIndex,
						new ObjectiveFunctionCoordinate (reflectedVertex, reflectedValue)
					);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			double penultimateHighestValue = vertexes.penultimateHighestValue();

			if (run instanceof DownhillSimplexRunDiagnostics) {
				try {
					((DownhillSimplexRunDiagnostics) run).setReflectedObjectiveFunctionCoordinate (
						iterationIndex,
						new ObjectiveFunctionCoordinate (
							vertexes.penultimateHighestValueVertex(),
							penultimateHighestValue
						)
					);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			double[] lowestValueVertex = vertexes.lowestValueVertex();

			double lowestValue = vertexes.lowestValue();

			if (run instanceof DownhillSimplexRunDiagnostics) {
				try {
					((DownhillSimplexRunDiagnostics) run).setReflectedObjectiveFunctionCoordinate (
						iterationIndex,
						new ObjectiveFunctionCoordinate (lowestValueVertex, lowestValue)
					);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (lowestValue <= reflectedValue && reflectedValue < penultimateHighestValue) {
				if (!vertexes.swapNodes (highestValue, reflectedValue, reflectedVertex)) {
					return null;
				}
			} else if (reflectedValue < lowestValue) {
				double[] expandedVertex = expandedVertex (reflectedVertex, centroidVertex);

				if (null == expandedVertex) {
					return null;
				}

				double expandedValue = Double.NaN;

				try {
					expandedValue = _objectiveFunction.evaluate (expandedVertex);
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}

				if (run instanceof DownhillSimplexRunDiagnostics) {
					try {
						((DownhillSimplexRunDiagnostics) run).setExpandedObjectiveFunctionCoordinate (
							iterationIndex,
							new ObjectiveFunctionCoordinate (expandedVertex, expandedValue)
						);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				if (expandedValue < reflectedValue) {
					if (!vertexes.swapNodes (highestValue, expandedValue, expandedVertex)) {
						return null;
					}
				} else {
					if (!vertexes.swapNodes (highestValue, reflectedValue, reflectedVertex)) {
						return null;
					}
				}
			} else {
				if (reflectedValue < highestValue) {
					double[] contractedVertex = contractedVertex (reflectedVertex, centroidVertex);

					if (null == contractedVertex) {
						return null;
					}

					double contractedValue = Double.NaN;

					try {
						contractedValue = _objectiveFunction.evaluate (contractedVertex);
					} catch (Exception e) {
						e.printStackTrace();

						return null;
					}

					if (run instanceof DownhillSimplexRunDiagnostics) {
						try {
							((DownhillSimplexRunDiagnostics) run).setContractedObjectiveFunctionCoordinate (
								iterationIndex,
								new ObjectiveFunctionCoordinate (contractedVertex, contractedValue)
							);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (contractedValue < reflectedValue) {
						if (!vertexes.swapNodes (highestValue, contractedValue, contractedVertex)) {
							return null;
						}
					} else {
						if (null == (vertexes = shrinkVertexes (lowestValueVertex))) {
							return null;
						}
					}
				} else {
					double[] contractedVertex = contractedVertex (highestValueVertex, centroidVertex);

					if (null == contractedVertex) {
						return null;
					}

					double contractedValue = Double.NaN;

					try {
						contractedValue = _objectiveFunction.evaluate (contractedVertex);
					} catch (Exception e) {
						e.printStackTrace();

						return null;
					}

					if (run instanceof DownhillSimplexRunDiagnostics) {
						try {
							((DownhillSimplexRunDiagnostics) run).setContractedObjectiveFunctionCoordinate (
								iterationIndex,
								new ObjectiveFunctionCoordinate (contractedVertex, contractedValue)
							);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (contractedValue < highestValue) {
						if (!vertexes.swapNodes (highestValue, contractedValue, contractedVertex)) {
							return null;
						}
					} else {
						if (null == (vertexes = shrinkVertexes (lowestValueVertex))) {
							return null;
						}
					}
				}
			}

			if (run instanceof DownhillSimplexRunDiagnostics) {
				try {
					((DownhillSimplexRunDiagnostics) run).setVertexes (iterationIndex, vertexes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			++iterationIndex;
		}

		double[] centroidVertex = vertexes.centroidVertex();

		try {
			return run.setOptimalObjectiveFunctionCoordinate (
				new ObjectiveFunctionCoordinate (
					centroidVertex,
					_objectiveFunction.evaluate (centroidVertex)
				)
			) ? run : null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @param prefix The JSON Prefix
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public String toString (
		final String prefix)
	{
		String dump = prefix + "{Vertex List => (";

		for (double[] vertex : _vertexList) {
			dump += NumberUtil.ArrayRow (vertex, 1, 4, false) + ", ";
		}

		return dump + "); OF: " + _objectiveFunction + "; Coefficients: " + _amoebaCoefficients + "}";
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public @Override String toString()
	{
		return toString ("");
	}

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		List<double[]> vertexList = new ArrayList<double[]>();

		vertexList.add (new double[] {-2., -2., -2.});

		vertexList.add (new double[] {-2., -2., 3.});

		vertexList.add (new double[] {-2., 3., -2.});

		vertexList.add (new double[] {-2., 3., 3.});

		vertexList.add (new double[] {3., -2., -2.});

		vertexList.add (new double[] {3., -2., 3.});

		vertexList.add (new double[] {3., 3., -2.});

		vertexList.add (new double[] {3., 3., 3.});

		RdToR1 optimizationFunction = new MultidimensionalRosenbrockCoupled (Rosenbrock.Standard(), 3);

		DownhillSimplex downhillSimplex = DownhillSimplex.Standard (optimizationFunction, vertexList, true);

		DownhillSimplexRun run = downhillSimplex.controlRun();

		ObjectiveFunctionCoordinate optimalCoordinate = run.optimalObjectiveFunctionCoordinate();

		System.out.println ("\t" + optimalCoordinate);

		if (run instanceof DownhillSimplexRunDiagnostics) {
			System.out.println (
				"\t" + ((DownhillSimplexRunDiagnostics) run).downhillSimplexIterationDiagnosticsMap()
			);
		}
	}
}
