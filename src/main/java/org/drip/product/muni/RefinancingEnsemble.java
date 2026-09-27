
package org.drip.product.muni;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.measure.statistics.UnivariateCentralMeasures;

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
 * <i>RefinancingEnsemble</i> holds the Ensemble of Bond PnL's incurred across multiple Paths and Dates by
 *  re-financing using simulated Muni Sub-markets, i.e., Tax-exempt, and Taxable Path Yield Curves. The
 * 	References are:
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

public class RefinancingEnsemble
{
	private TreeMap<JulianDate, Integer> _dateTaxableNegativeArbitrageMap = null;
	private TreeMap<JulianDate, Integer> _dateTaxExemptNegativeArbitrageMap = null;
	private TreeMap<JulianDate, List<RefinancingPathEntry>> _dateToPathEntryListMap = null;
	private TreeMap<JulianDate, UnivariateCentralMeasures> _taxablePnLCentralMeasuresMap = null;
	private TreeMap<JulianDate, UnivariateCentralMeasures> _taxExemptPnLCentralMeasuresMap = null;
	private TreeMap<JulianDate, UnivariateCentralMeasures>
		_taxableGovvieCurveDirtyPriceCentralMeasuresMap = null;
	private TreeMap<JulianDate, UnivariateCentralMeasures>
		_taxExemptGovvieCurveDirtyPriceCentralMeasuresMap = null;

	/**
	 * Empty <i>RefinancingEnsemble</i> Constructor
	 */

	public RefinancingEnsemble()
	{
		_dateTaxableNegativeArbitrageMap = new TreeMap<JulianDate, Integer>();

		_dateTaxExemptNegativeArbitrageMap = new TreeMap<JulianDate, Integer>();

		_dateToPathEntryListMap = new TreeMap<JulianDate, List<RefinancingPathEntry>>();

		_taxablePnLCentralMeasuresMap = new TreeMap<JulianDate, UnivariateCentralMeasures>();

		_taxExemptPnLCentralMeasuresMap = new TreeMap<JulianDate, UnivariateCentralMeasures>();

		_taxableGovvieCurveDirtyPriceCentralMeasuresMap =
			new TreeMap<JulianDate, UnivariateCentralMeasures>();

		_taxExemptGovvieCurveDirtyPriceCentralMeasuresMap =
			new TreeMap<JulianDate, UnivariateCentralMeasures>();
	}

	/**
	 * Retrieve the Map of Date to Re-financing Path List
	 * 
	 * @return Map of Date to Re-financing Path List
	 */

	public TreeMap<JulianDate, List<RefinancingPathEntry>> dateToPathEntryListMap()
	{
		return _dateToPathEntryListMap;
	}

	/**
	 * Retrieve the Date Map of Tax-exempt Dirty Price Central Measures
	 * 
	 * @return Date Map of Tax-exempt Dirty Price Central Measures
	 */

	public TreeMap<JulianDate, UnivariateCentralMeasures> taxExemptGovvieCurveDirtyPriceCentralMeasuresMap()
	{
		return _taxExemptGovvieCurveDirtyPriceCentralMeasuresMap;
	}

	/**
	 * Retrieve the Date Map of Taxable Dirty Price Central Measures
	 * 
	 * @return Date Map of Taxable Dirty Price Central Measures
	 */

	public TreeMap<JulianDate, UnivariateCentralMeasures> taxableGovvieCurveDirtyPriceCentralMeasuresMap()
	{
		return _taxableGovvieCurveDirtyPriceCentralMeasuresMap;
	}

	/**
	 * Retrieve the Tax-exempt PnL Central Measures
	 * 
	 * @return Tax-exempt PnL Central Measures
	 */

	public TreeMap<JulianDate, UnivariateCentralMeasures> taxExemptPnLCentralMeasuresMap()
	{
		return _taxExemptPnLCentralMeasuresMap;
	}

	/**
	 * Retrieve the Taxable PnL Central Measures
	 * 
	 * @return Taxable PnL Central Measures
	 */

	public TreeMap<JulianDate, UnivariateCentralMeasures> taxablePnLCentralMeasuresMap()
	{
		return _taxablePnLCentralMeasuresMap;
	}

	/**
	 * Retrieve the Map of the Simulation Date to Tax-exempt Negative Arbitrage Indicator at the Target Date
	 * 
	 * @return Map of the Simulation Date to Tax-exempt Negative Arbitrage Indicator at the Target Date
	 */

	public TreeMap<JulianDate, Integer> dateTaxExemptNegativeArbitrageMap()
	{
		return _dateTaxExemptNegativeArbitrageMap;
	}

	/**
	 * Retrieve the Map of the Simulation Date to Taxable Negative Arbitrage Indicator at the Target Date
	 * 
	 * @return Map of the Simulation Date to Taxable Negative Arbitrage Indicator at the Target Date
	 */

	public TreeMap<JulianDate, Integer> dateTaxableNegativeArbitrageMap()
	{
		return _dateTaxableNegativeArbitrageMap;
	}

	/**
	 * Add the <i>RefinancingPathEntry</i> Instance at the Specified Date
	 * 
	 * @param date Date
	 * @param refinancingPathEntry <i>RefinancingPathEntry</i> Instance
	 * 
	 * @return TRUE -The <i>RefinancingPathEntry</i> Instance successfully added at the Specified Date
	 */

	public boolean add (
		final JulianDate date,
		final RefinancingPathEntry refinancingPathEntry)
	{
		if (null == date || null == refinancingPathEntry) {
			return false;
		}

		if (!_dateToPathEntryListMap.containsKey (date)) {
			_dateToPathEntryListMap.put (date, new ArrayList<RefinancingPathEntry>());
		}

		_dateToPathEntryListMap.get (date).add (refinancingPathEntry);

		return true;
	}

	/**
	 * Update the <i>UnivariateCentralMeasures</i> Maps
	 * 
	 * @return TRUE - The <i>UnivariateCentralMeasures</i> Maps successfully updated
	 */

	public boolean updateCentralMeasures()
	{
		if (_dateToPathEntryListMap.isEmpty()) {
			return false;
		}

		for (JulianDate date : _dateToPathEntryListMap.keySet()) {
			List<Double> taxExemptGovvieCurveDirtyPriceList = new ArrayList<Double>();

			List<Double> taxableGovvieCurveDirtyPriceList = new ArrayList<Double>();

			List<Double> taxExemptPnLList = new ArrayList<Double>();

			List<Double> taxablePnLList = new ArrayList<Double>();

			for (RefinancingPathEntry refinancingPathPnLEntry : _dateToPathEntryListMap.get (date)) {
				taxExemptGovvieCurveDirtyPriceList.add (
					refinancingPathPnLEntry.taxExemptGovvieCurveCleanPrice()
				);

				taxableGovvieCurveDirtyPriceList.add (
					refinancingPathPnLEntry.taxableGovvieCurveCleanPrice()
				);

				taxExemptPnLList.add (refinancingPathPnLEntry.taxExemptPnL());

				taxablePnLList.add (refinancingPathPnLEntry.taxablePnL());
			}

			_taxExemptGovvieCurveDirtyPriceCentralMeasuresMap.put (
				date,
				UnivariateCentralMeasures.FromList (taxExemptGovvieCurveDirtyPriceList)
			);

			_taxableGovvieCurveDirtyPriceCentralMeasuresMap.put (
				date,
				UnivariateCentralMeasures.FromList (taxableGovvieCurveDirtyPriceList)
			);

			_taxExemptPnLCentralMeasuresMap.put (
				date,
				UnivariateCentralMeasures.FromList (taxExemptPnLList)
			);

			_taxablePnLCentralMeasuresMap.put (date, UnivariateCentralMeasures.FromList (taxablePnLList));
		}

		return true;
	}

	/**
	 * Update the Tax-exempt Negative Arbitrage Map
	 * 
	 * @param date Date 
	 * @param negativeArbitrageIndicator Negative Arbitrage Indicator
	 * 
	 * @return Tax-exempt Negative Arbitrage Map
	 */

	public boolean updateTaxExemptNegativeArbitrageMap (
		final JulianDate date,
		final boolean negativeArbitrageIndicator)
	{
		if (null == date) {
			return false;
		}

		if (!_dateTaxExemptNegativeArbitrageMap.containsKey (date)) {
			_dateTaxExemptNegativeArbitrageMap.put (date, 0);
		}

		if (negativeArbitrageIndicator) {
			_dateTaxExemptNegativeArbitrageMap.put (date, _dateTaxExemptNegativeArbitrageMap.get (date) + 1);
		}

		return true;
	}

	/**
	 * Update the Taxable Negative Arbitrage Map
	 * 
	 * @param date Date 
	 * @param negativeArbitrageIndicator Negative Arbitrage Indicator
	 * 
	 * @return Taxable Negative Arbitrage Map
	 */

	public boolean updateTaxableNegativeArbitrageMap (
		final JulianDate date,
		final boolean negativeArbitrageIndicator)
	{
		if (null == date) {
			return false;
		}

		if (!_dateTaxableNegativeArbitrageMap.containsKey (date)) {
			_dateTaxableNegativeArbitrageMap.put (date, 0);
		}

		if (negativeArbitrageIndicator) {
			_dateTaxableNegativeArbitrageMap.put (date, _dateTaxableNegativeArbitrageMap.get (date) + 1);
		}

		return true;
	}
}
