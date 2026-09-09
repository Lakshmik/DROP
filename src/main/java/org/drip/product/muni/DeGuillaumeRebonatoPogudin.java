
package org.drip.product.muni;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drip.analytics.date.JulianDate;
import org.drip.measure.dynamics.DiffusionEvaluatorOrnsteinUhlenbeck;
import org.drip.measure.realization.DiffusionEvolver;
import org.drip.measure.realization.JumpDiffusionEdgeUnit;
import org.drip.measure.realization.JumpDiffusionVertex;
import org.drip.numerical.common.NumberUtil;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
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
 * <i>DeGuillaumeRebonatoPogudin</i> evolves the Market Yield Term Structure for Muni Sub-markets, i.e.,
 *  Tax-exempt, Taxable, and Escrow Yield Curves using the de Guillaume, Rebonato, and Pogudin (201)
 *  scheme. Default Evolution is using Ornstein-Uhlenbeck Evolver. The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Ang, A., R. C. Green, and Y. Xing (2013): <i>Advance Re-fundings of Municipal Bonds</i>
 *  			https://www.nber.org/papers/w19459
 *  	</li>
 *  	<li>
 *  		de Guillaume, N., R. Rebonato, and A. Pogudin (2013): The Nature of the Dependence of the
 *  			Magnitude of the Rates Moves on the Rates Levels: A Universal Relationship <i>Quantitative
 *  			Finance</i> <b>13 (3)</b> 351-367
 *  	</li>
 *  	<li>
 *  		Orr, P., and D. de la Nuez (2013): <i>The Right and Wrong Models for Evaluating Callable
 *  			Municipal Bonds</i> <b>eSSRN</b>
 *  	</li>
 *  	<li>
 *  		Rebonato, R. (2003): <i>Term Structure Models: A Review</i>
 *  			https://dept.math.lsa.umich.edu/~conlon/math623/rebonato_review.pdf
 *  	</li>
 *  	<li>
 *  		Rebonato, R., and S. K. Nawalkha (2011): <i>What Interest Rate Models to Use? Buy Side versus
 *  			Sell Side</i> <b>eSSRN</b>
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/README.md">Product Components/Baskets for Credit, FRA, FX, Govvie, Rates, and Option Asset Classes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/muni/README.md">Refunding and Optimal Exercise Mechanics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class DeGuillaumeRebonatoPogudin
{
	private String _currency = "";
	private Map<String, DiffusionEvolver> _tenorEscrowEvolverMap = null;
	private Map<String, DiffusionEvolver> _tenorTaxableEvolverMap = null;
	private Map<String, DiffusionEvolver> _tenorTaxExemptEvolverMap = null;

	/**
	 * Construct a Standard Instance of <i>DeGuillaumeRebonatoPogudin</i>
	 * 
	 * @param currency Currency
	 * @param burstiness The Burstiness Parameter
	 * @param relaxationTime The Relaxation Time
	 * @param marketYield <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 * 
	 * @return Standard Instance of <i>DeGuillaumeRebonatoPogudin</i>
	 */

	public static final DeGuillaumeRebonatoPogudin Standard (
		final String currency,
		final double burstiness,
		final double relaxationTime,
		final DeGuillaumeRebonatoPogudinMarketYield marketYield)
	{
		if (null == marketYield) {
			return null;
		}

		Map<String, DiffusionEvolver> tenorEscrowEvolverMap = new HashMap<String, DiffusionEvolver>();

		Map<String, DiffusionEvolver> tenorTaxableEvolverMap = new HashMap<String, DiffusionEvolver>();

		Map<String, DiffusionEvolver> tenorTaxExemptEvolverMap = new HashMap<String, DiffusionEvolver>();

		MarketYieldTermStructure escrowMarketYieldTermStructure =
			marketYield.escrowMarketYieldTermStructure();

		MarketYieldTermStructure taxableMarketYieldTermStructure =
			marketYield.taxableMarketYieldTermStructure();

		MarketYieldTermStructure taxExemptMarketYieldTermStructure =
			marketYield.taxExemptMarketYieldTermStructure();

		try {
			for (String tenorKey : marketYield.tenorKeySet()) {
				tenorTaxExemptEvolverMap.put (
					tenorKey,
					new DiffusionEvolver (
						DiffusionEvaluatorOrnsteinUhlenbeck.Standard (
							taxExemptMarketYieldTermStructure.infiniteHorizonTenorValueMap().get (tenorKey),
							burstiness,
							relaxationTime
						)
					)
				);

				tenorTaxableEvolverMap.put (
					tenorKey,
					new DiffusionEvolver (
						DiffusionEvaluatorOrnsteinUhlenbeck.Standard (
							taxableMarketYieldTermStructure.infiniteHorizonTenorValueMap().get (tenorKey),
							burstiness,
							relaxationTime
						)
					)
				);

				tenorEscrowEvolverMap.put (
					tenorKey,
					new DiffusionEvolver (
						DiffusionEvaluatorOrnsteinUhlenbeck.Standard (
							escrowMarketYieldTermStructure.infiniteHorizonTenorValueMap().get (tenorKey),
							burstiness,
							relaxationTime
						)
					)
				);
			}

			DeGuillaumeRebonatoPogudin deGuillaumeRebonatoPogudin = new DeGuillaumeRebonatoPogudin (
				currency,
				tenorTaxExemptEvolverMap,
				tenorTaxableEvolverMap,
				tenorEscrowEvolverMap
			);

			return deGuillaumeRebonatoPogudin;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static final DeGuillaumeRebonatoPogudinPath InitializeRun (
		final JulianDate spotDate,
		final DeGuillaumeRebonatoPogudinMarketYield marketYield)
	{
		if (null == spotDate || null == marketYield) {
			return null;
		}

		DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun = new DeGuillaumeRebonatoPogudinPath();

		return deGuillaumeRebonatoPogudinRun.addTaxExemptDateTenorYieldRealization (
			spotDate,
			marketYield.taxExemptMarketYieldTermStructure().spotTenorValueMap()
		) && deGuillaumeRebonatoPogudinRun.addTaxableDateTenorYieldRealization (
			spotDate,
			marketYield.taxableMarketYieldTermStructure().spotTenorValueMap()
		) && deGuillaumeRebonatoPogudinRun.addEscrowDateTenorYieldRealization (
			spotDate,
			marketYield.escrowMarketYieldTermStructure().spotTenorValueMap()
		) ? deGuillaumeRebonatoPogudinRun : null;
	}

	private static final JumpDiffusionEdgeUnit[] JumpDiffusionEdgeUnitArray (
		final double[] timeIncrementArray)
	{
		JumpDiffusionEdgeUnit[] jumpDiffusionEdgeUnitArray =
			new JumpDiffusionEdgeUnit[timeIncrementArray.length];

		for (int incrementIndex = 0; incrementIndex < timeIncrementArray.length; ++incrementIndex) {
			jumpDiffusionEdgeUnitArray[incrementIndex] =
				JumpDiffusionEdgeUnit.GaussianDiffusion (timeIncrementArray[incrementIndex]);
		}

		return jumpDiffusionEdgeUnitArray;
	}

	private static final double[] TimeIncrementArray (
		final double increment,
		final double terminalTime)
	{
		int stepCount = (int) (terminalTime / increment);
		double[] timeIncrementArray = new double[stepCount];

		for (int stepIndex = 0; stepIndex < stepCount; ++stepIndex) {
			timeIncrementArray[stepIndex] = increment;
		}

		return timeIncrementArray;
	}

	private static final double[] TimeIncrementArray (
		final JulianDate spotDate,
		final List<JulianDate> simulationDateArray)
	{
		int simulationCount = simulationDateArray.size();

		int startDateJulian = spotDate.julian();

		double yearsPerDay = 1./ 365.25;
		double[] timeIncrementArray = new double[simulationCount];

		for (int incrementIndex = 0; incrementIndex < simulationCount; ++incrementIndex) {
			int simulationDateJulian = simulationDateArray.get (incrementIndex).julian();

			timeIncrementArray[incrementIndex] = yearsPerDay * (simulationDateJulian - startDateJulian);
			startDateJulian = simulationDateJulian;
		}

		return timeIncrementArray;
	}

	private boolean taxExemptYieldPathsRun (
		final JulianDate spotDate,
		final DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun,
		final Map<String, Double> spotTenorValueMap,
		final Set<String> tenorKeySet,
		final double[] timeIncrementArray)
	{
		for (String tenorKey : tenorKeySet) {
			try {
				JumpDiffusionVertex[] jumpDiffusionVertexArray = _tenorTaxExemptEvolverMap.get (
					tenorKey
				).vertexSequence (
					new JumpDiffusionVertex (0., spotTenorValueMap.get (tenorKey), 0., false),
					JumpDiffusionEdgeUnitArray (timeIncrementArray),
					timeIncrementArray
				);

				if (null == jumpDiffusionVertexArray || 0 == jumpDiffusionVertexArray.length) {
					return false;
				}

				for (int vertexIndex = 0; vertexIndex < jumpDiffusionVertexArray.length; ++vertexIndex) {
					deGuillaumeRebonatoPogudinRun.addTaxExemptDateTenorYieldRealization (
						spotDate.addDays (
							(int) (365.25 * jumpDiffusionVertexArray[vertexIndex].time() + 0.5)
						),
						tenorKey,
						jumpDiffusionVertexArray[vertexIndex].value()
					);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private boolean taxableYieldPathsRun (
		final JulianDate spotDate,
		final DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun,
		final Map<String, Double> spotTenorValueMap,
		final Set<String> tenorKeySet,
		final double[] timeIncrementArray)
	{
		for (String tenorKey : tenorKeySet) {
			try {
				JumpDiffusionVertex[] jumpDiffusionVertexArray = _tenorTaxableEvolverMap.get (
					tenorKey
				).vertexSequence (
					new JumpDiffusionVertex (0., spotTenorValueMap.get (tenorKey), 0., false),
					JumpDiffusionEdgeUnitArray (timeIncrementArray),
					timeIncrementArray
				);

				if (null == jumpDiffusionVertexArray || 0 == jumpDiffusionVertexArray.length) {
					return false;
				}

				for (int vertexIndex = 0; vertexIndex < jumpDiffusionVertexArray.length; ++vertexIndex) {
					deGuillaumeRebonatoPogudinRun.addTaxableDateTenorYieldRealization (
						spotDate.addDays (
							(int) (365.25 * jumpDiffusionVertexArray[vertexIndex].time() + 0.5)
						),
						tenorKey,
						jumpDiffusionVertexArray[vertexIndex].value()
					);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private boolean escrowYieldPathsRun (
		final JulianDate spotDate,
		final DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun,
		final Map<String, Double> spotTenorValueMap,
		final Set<String> tenorKeySet,
		final double[] timeIncrementArray)
	{
		for (String tenorKey : tenorKeySet) {
			try {
				JumpDiffusionVertex[] jumpDiffusionVertexArray = _tenorEscrowEvolverMap.get (
					tenorKey
				).vertexSequence (
					new JumpDiffusionVertex (0., spotTenorValueMap.get (tenorKey), 0., false),
					JumpDiffusionEdgeUnitArray (timeIncrementArray),
					timeIncrementArray
				);

				if (null == jumpDiffusionVertexArray || 0 == jumpDiffusionVertexArray.length) {
					return false;
				}

				for (int vertexIndex = 0; vertexIndex < jumpDiffusionVertexArray.length; ++vertexIndex) {
					deGuillaumeRebonatoPogudinRun.addEscrowDateTenorYieldRealization (
						spotDate.addDays (
							(int) (365.25 * jumpDiffusionVertexArray[vertexIndex].time() + 0.5)
						),
						tenorKey,
						jumpDiffusionVertexArray[vertexIndex].value()
					);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/**
	 * <i>DeGuillaumeRebonatoPogudin</i> Constructor
	 * 
	 * @param currency Currency
	 * @param tenorTaxExemptEvolverMap Map of Tenor to Tax-exempt <i>DiffusionEvolver</> Evolver
	 * @param tenorTaxableEvolverMap Map of Tenor to Taxable <i>DiffusionEvolver</> Evolver
	 * @param tenorEscrowEvolverMap Map of Tenor to Escrow <i>DiffusionEvolver</> Evolver
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public DeGuillaumeRebonatoPogudin (
		final String currency,
		final Map<String, DiffusionEvolver> tenorTaxExemptEvolverMap,
		final Map<String, DiffusionEvolver> tenorTaxableEvolverMap,
		final Map<String, DiffusionEvolver> tenorEscrowEvolverMap)
		throws Exception
	{
		if (null == (_currency = currency) || _currency.isEmpty() ||
			null == (_tenorTaxExemptEvolverMap = tenorTaxExemptEvolverMap) ||
			null == (_tenorTaxableEvolverMap = tenorTaxableEvolverMap) ||
			null == (_tenorEscrowEvolverMap = tenorEscrowEvolverMap))
		{
			throw new Exception ("DeGuillaumeRebonatoPogudin Constructor => Invalid Inputs");
		}

		int size = _tenorTaxExemptEvolverMap.size();

		if (0 == size || size != _tenorTaxableEvolverMap.size() || size != _tenorEscrowEvolverMap.size()) {
			throw new Exception ("DeGuillaumeRebonatoPogudin Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Currency
	 * 
	 * @return Currency
	 */

	public String currency()
	{
		return _currency;
	}

	/**
	 * Retrieve the Map of Tenor to Tax-exempt <i>DiffusionEvolver</> Evolver
	 * 
	 * @return Map of Tenor to Tax-exempt <i>DiffusionEvolver</> Evolver
	 */

	public Map<String, DiffusionEvolver> tenorTaxExemptEvolverMap()
	{
		return _tenorTaxExemptEvolverMap;
	}

	/**
	 * Retrieve the Map of Tenor to Taxable <i>DiffusionEvolver</> Evolver
	 * 
	 * @return Map of Tenor to Taxable <i>DiffusionEvolver</> Evolver
	 */

	public Map<String, DiffusionEvolver> tenorTaxableEvolverMap()
	{
		return _tenorTaxableEvolverMap;
	}

	/**
	 * Retrieve the Map of Tenor to Escrow <i>DiffusionEvolver</> Evolver
	 * 
	 * @return Map of Tenor to Escrow <i>DiffusionEvolver</> Evolver
	 */

	public Map<String, DiffusionEvolver> tenorEscrowEvolverMap()
	{
		return _tenorEscrowEvolverMap;
	}

	/**
	 * Generate an Instance of <i>DeGuillaumeRebonatoPogudinRun</i> Instance
	 * 
	 * @param marketYield <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 * @param spotDate Spot Date
	 * @param increment Evolution Time Increment
	 * @param terminalTime Evolution Termination Time
	 * 
	 * @return <i>DeGuillaumeRebonatoPogudinRun</i> Instance
	 */

	public DeGuillaumeRebonatoPogudinPath evolve (
		final DeGuillaumeRebonatoPogudinMarketYield marketYield,
		final JulianDate spotDate,
		final double increment,
		final double terminalTime)
	{
		if (null == marketYield ||
			null == spotDate ||
			!NumberUtil.IsValid (increment) || 0. >= increment ||
			!NumberUtil.IsValid (terminalTime) || terminalTime < increment)
		{
			return null;
		}

		DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun = InitializeRun (spotDate, marketYield);

		if (null == deGuillaumeRebonatoPogudinRun) {
			return null;
		}

		Set<String> tenorKeySet = marketYield.tenorKeySet();

		double[] timeIncrementArray = TimeIncrementArray (increment, terminalTime);

		return taxExemptYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.taxExemptMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) && taxableYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.taxableMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) && escrowYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.escrowMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) ? deGuillaumeRebonatoPogudinRun : null;
	}

	/**
	 * Generate an Instance of <i>DeGuillaumeRebonatoPogudinRun</i> Instance
	 * 
	 * @param marketYield <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 * @param spotDate Spot Date
	 * @param simulationDateList List of Ascending Simulation Dates
	 * 
	 * @return <i>DeGuillaumeRebonatoPogudinRun</i> Instance
	 */

	public DeGuillaumeRebonatoPogudinPath evolve (
		final DeGuillaumeRebonatoPogudinMarketYield marketYield,
		final JulianDate spotDate,
		final List<JulianDate> simulationDateList)
	{
		if (null == marketYield ||
			null == spotDate ||
			null == simulationDateList || 0 == simulationDateList.size())
		{
			return null;
		}

		DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinRun = InitializeRun (spotDate, marketYield);

		if (null == deGuillaumeRebonatoPogudinRun) {
			return null;
		}

		Set<String> tenorKeySet = marketYield.tenorKeySet();

		double[] timeIncrementArray = TimeIncrementArray (spotDate, simulationDateList);

		return taxExemptYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.taxExemptMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) && taxableYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.taxableMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) && escrowYieldPathsRun (
			spotDate,
			deGuillaumeRebonatoPogudinRun,
			marketYield.escrowMarketYieldTermStructure().spotTenorValueMap(),
			tenorKeySet,
			timeIncrementArray
		) && deGuillaumeRebonatoPogudinRun.setUpGovvieCurveMap (
			_currency
		) ? deGuillaumeRebonatoPogudinRun : null;
	}
}
