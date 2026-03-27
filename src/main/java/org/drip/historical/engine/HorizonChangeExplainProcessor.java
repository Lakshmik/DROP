
package org.drip.historical.engine;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.historical.attribution.PositionMarketSnap;
import org.drip.numerical.common.NumberUtil;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.definition.Component;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * Copyright (C) 2025 Lakshmi Krishnamurthy
 * Copyright (C) 2024 Lakshmi Krishnamurthy
 * Copyright (C) 2023 Lakshmi Krishnamurthy
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
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
 * <i>HorizonChangeExplainProcessor</i> holds the Stubs associated with the Computation of the Horizon
 * 	Position Change Components for the given Product. It provides the following Functionality:
 *
 *  <ul>
 * 		<li>Retrieve the Component</li>
 * 		<li>Retrieve the Component Settle Lag</li>
 * 		<li>Retrieve the Component Market Measure Name</li>
 * 		<li>Retrieve the Component Market Measure Value</li>
 * 		<li>Retrieve the First Date of the Horizon Change</li>
 * 		<li>Retrieve the First Date's Market Parameters</li>
 * 		<li>Retrieve the Second Date of the Horizon Change</li>
 * 		<li>Retrieve the Second Date's Market Parameters</li>
 * 		<li>Retrieve the Map of the Roll Down Market Parameters</li>
 * 		<li>Generate the Map of the Roll Down Market Quote Metrics</li>
 * 		<li>Generate the Roll Up Version of the Quote Metric</li>
 * 		<li>Generate and Snap Relevant Fields from the T1 Market Valuation Parameters</li>
 * 		<li>Update the Fixings (if any) to the Second Market Parameters</li>
 * 		<li>Generate and Snap Relevant Fields from the T2 Market Valuation Parameters</li>
 * 		<li>Generate the Horizon Differential Metrics Map</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/engine/README.md">Product Horizon Change Explain Engine</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public abstract class HorizonChangeExplainProcessor
{
	private int _settleLag = -1;
	private JulianDate _t1 = null;
	private JulianDate _t2 = null;
	private String _marketMeasure = "";
	private Component _component = null;
	private double _marketMeasureValue = Double.NaN;
	private CurveSurfaceQuoteContainer _t1CurveSurfaceQuoteContainer = null;
	private CurveSurfaceQuoteContainer _t2CurveSurfaceQuoteContainer = null;
	private CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> _curveSurfaceQuoteContainerRollDownMap = null;

	protected HorizonChangeExplainProcessor (
		final Component component,
		final int settleLag,
		final String marketMeasure,
		final double marketMeasureValue,
		final JulianDate t1,
		final JulianDate t2,
		final CurveSurfaceQuoteContainer t1CurveSurfaceQuoteContainer,
		final CurveSurfaceQuoteContainer t2CurveSurfaceQuoteContainer,
		final CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> curveSurfaceQuoteContainerRollDownMap)
		throws Exception
	{
		if (null == (_component = component) || 0 > (_settleLag = settleLag) ||
			null == (_marketMeasure = marketMeasure) || _marketMeasure.isEmpty() ||
			!NumberUtil.IsValid (_marketMeasureValue = marketMeasureValue) ||
			null == (_t1 = t1) || null == (_t2 = t2) || _t2.julian() <= _t1.julian() ||
			null == (_t1CurveSurfaceQuoteContainer = t1CurveSurfaceQuoteContainer) ||
			null == (_t2CurveSurfaceQuoteContainer = t2CurveSurfaceQuoteContainer) ||
			null == (_curveSurfaceQuoteContainerRollDownMap = curveSurfaceQuoteContainerRollDownMap))
		{
			throw new Exception ("HorizonChangeExplainProcessor Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Component
	 * 
	 * @return The Component
	 */

	public Component component()
	{
		return _component;
	}

	/**
	 * Retrieve the Component Settle Lag
	 * 
	 * @return The Component Settle Lag
	 */

	public int settleLag()
	{
		return _settleLag;
	}

	/**
	 * Retrieve the Component Market Measure Name
	 * 
	 * @return The Component Market Measure Name
	 */

	public String marketMeasureName()
	{
		return _marketMeasure;
	}

	/**
	 * Retrieve the Component Market Measure Value
	 * 
	 * @return The Component Market Measure Value
	 */

	public double marketMeasureValue()
	{
		return _marketMeasureValue;
	}

	/**
	 * Retrieve the First Date of the Horizon Change
	 * 
	 * @return The First Date of the Horizon Change
	 */

	public JulianDate t1()
	{
		return _t1;
	}

	/**
	 * Retrieve the First Date's Market Parameters
	 * 
	 * @return The First Date's Market Parameters
	 */

	public CurveSurfaceQuoteContainer t1MarketParameters()
	{
		return _t1CurveSurfaceQuoteContainer;
	}

	/**
	 * Retrieve the Second Date of the Horizon Change
	 * 
	 * @return The Second Date of the Horizon Change
	 */

	public JulianDate t2()
	{
		return _t2;
	}

	/**
	 * Retrieve the Second Date's Market Parameters
	 * 
	 * @return The Second Date's Market Parameters
	 */

	public CurveSurfaceQuoteContainer secondMarketParameters()
	{
		return _t2CurveSurfaceQuoteContainer;
	}

	/**
	 * Retrieve the Map of the Roll Down Market Parameters
	 * 
	 * @return Map of the Roll Down Market Parameters
	 */

	public CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> curveSurfaceQuoteContainerRollDownMap()
	{
		return _curveSurfaceQuoteContainerRollDownMap;
	}

	/**
	 * Generate the Map of the Roll Down Market Quote Metrics
	 * 
	 * @return Map of the Roll Down Market Quote Metrics
	 */

	public MarketMeasureRollDown rollDownMeasureMap()
	{
		ValuationParams rollDownValuationParams = ValuationParams.Spot (
			_t2.addBusDays (_settleLag, _component.payCurrency()).julian()
		);

		MarketMeasureRollDown marketMeasureRollDown = null;

		for (String rollDownTenor : _curveSurfaceQuoteContainerRollDownMap.keySet()) {
			Map<String, Double> componentMeasureMap = _component.value (
				rollDownValuationParams,
				null,
				_curveSurfaceQuoteContainerRollDownMap.get (rollDownTenor),
				null
			);

			if (null == componentMeasureMap || !componentMeasureMap.containsKey (_marketMeasure)) {
				return null;
			}

			if ("Native".equalsIgnoreCase (rollDownTenor)) {
				try {
					marketMeasureRollDown =
						new MarketMeasureRollDown (componentMeasureMap.get (_marketMeasure));
				} catch (Exception e) {
					e.printStackTrace();

					return null;
				}
			} else {
				marketMeasureRollDown.add (rollDownTenor, componentMeasureMap.get (_marketMeasure));
			}
		}

		return marketMeasureRollDown;
	}

	/**
	 * Generate the Roll Up Version of the Quote Metric
	 * 
	 * @return The Roll Up Version of the Quote Metric
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double metricRollUp()
		throws Exception
	{
		Map<String, Double> componentMeasureMap = _component.value (
			ValuationParams.Spot (_t2.addBusDays (_settleLag, _component.payCurrency()).julian()),
			null,
			_t1CurveSurfaceQuoteContainer,
			null
		);

		if (null == componentMeasureMap || !componentMeasureMap.containsKey (_marketMeasure)) {
			throw new Exception ("HorizonChangeExplainProcessor::metricRollUp => Invalid Inputs");
		}

		return componentMeasureMap.get (_marketMeasure);
	}

	/**
	 * Generate and Snap Relevant Fields from the T1 Market Valuation Parameters
	 * 
	 * @return The T1 Market Parameters Valuation Snapshot
	 */

	public abstract PositionMarketSnap t1PositionMarketSnap();

	/**
	 * Update the Fixings (if any) to the Second Market Parameters
	 * 
	 * @return TRUE - The Fixings were successfully updated to the Second Market Parameters
	 */

	public abstract boolean updateFixings();

	/**
	 * Generate and Snap Relevant Fields from the T2 Market Valuation Parameters
	 * 
	 * @return The T2 Market Parameters Valuation Snapshot
	 */

	public abstract PositionMarketSnap t2PositionMarketSnap();

	/**
	 * Generate the Horizon Differential Metrics Map
	 * 
	 * @param t1PositionMarketSnap The First Position Market Snap
	 * @param t2PositionMarketSnap The Second Position Market Snap
	 * 
	 * @return The Horizon Differential Metrics Map
	 */

	public abstract CaseInsensitiveHashMap<Double> crossHorizonDifferentialMetrics (
		final PositionMarketSnap t1PositionMarketSnap,
		final PositionMarketSnap t2PositionMarketSnap
	);
}
