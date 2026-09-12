
package org.drip.product.muni;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drip.analytics.date.JulianDate;
import org.drip.market.issue.TreasurySettingContainer;
import org.drip.numerical.common.NumberUtil;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.credit.BondComponent;
import org.drip.state.creator.ScenarioGovvieCurveBuilder;
import org.drip.state.govvie.GovvieCurve;

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
 * <i>DeGuillaumeRebonatoPogudinMarketSettings</i> holds the Market Yield Term Structure for Muni
 *  Sub-markets, i.e., Tax-exempt, Taxable, and Escrow Yield Curves. The References are:
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

public class DeGuillaumeRebonatoPogudinMarketSettings
{
	private String _currency = "";
	private JulianDate _spotDate = null;
	private MarketYieldTermStructure _escrowMarketYieldTermStructure = null;
	private MarketYieldTermStructure _taxableMarketYieldTermStructure = null;
	private MarketYieldTermStructure _taxExemptMarketYieldTermStructure = null;

	/**
	 * Construct a <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance from the Tax Rate
	 * 
	 * @param currency Yield Currency
	 * @param spotDate Spot Date
	 * @param taxExemptMarketYieldTermStructure Tax-exempt <i>MarketYieldTermStructure</i>
	 * @param escrowMarketYieldTermStructure Escrow (i.e., SGLS) <i>MarketYieldTermStructure</i>
	 * @param taxRate Tax Rate
	 * 
	 * @return <i>DeGuillaumeRebonatoPogudinMarketYield</i> Instance
	 */

	public static final DeGuillaumeRebonatoPogudinMarketSettings FromTaxRate (
		final String currency,
		final JulianDate spotDate,
		final MarketYieldTermStructure taxExemptMarketYieldTermStructure,
		final MarketYieldTermStructure escrowMarketYieldTermStructure,
		final double taxRate)
	{
		if (null == taxExemptMarketYieldTermStructure ||
			!NumberUtil.IsValid (taxRate) || 0. > taxRate || 1. < taxRate)
		{
			return null;
		}

		double taxScaler = 1. / (1. - taxRate);

		Map<String, Double> taxableSpotTenorValueMap = new HashMap<String, Double>();

		Map<String, Double> taxableInfiniteHorizonTenorValueMap = new HashMap<String, Double>();

		Map<String, Double> taxExemptSpotTenorValueMap =
			taxExemptMarketYieldTermStructure.spotTenorValueMap();

		Map<String, Double> taxExemptInfiniteHorizonTenorValueMap =
			taxExemptMarketYieldTermStructure.infiniteHorizonTenorValueMap();

		for (String tenor : taxExemptSpotTenorValueMap.keySet()) {
			taxableSpotTenorValueMap.put (tenor, taxExemptSpotTenorValueMap.get (tenor) * taxScaler);
		}

		for (String tenor : taxExemptInfiniteHorizonTenorValueMap.keySet()) {
			taxableInfiniteHorizonTenorValueMap.put (
				tenor,
				taxExemptInfiniteHorizonTenorValueMap.get (tenor) * taxScaler
			);
		}

		try {
			return new DeGuillaumeRebonatoPogudinMarketSettings (
				currency,
				spotDate,
				taxExemptMarketYieldTermStructure,
				new MarketYieldTermStructure (
					taxableSpotTenorValueMap,
					taxExemptInfiniteHorizonTenorValueMap
				),
				escrowMarketYieldTermStructure
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static final double[] InitialYieldArray (
		final MarketYieldTermStructure marketYieldTermStructure)
	{
		Map<String, Double> spotTenorValueMap = marketYieldTermStructure.spotTenorValueMap();

		Set<String> tenorKeySet = marketYieldTermStructure.tenorKeySet();

		double[] initialYieldArray = new double[tenorKeySet.size()];

		int i = 0;

		for (String tenorKey : tenorKeySet) {
			initialYieldArray[i++] = spotTenorValueMap.get (tenorKey);
		}

		return initialYieldArray;
	}

	private int[] tenorJulianDateArray()
	{
		int i = 0;

		Set<String> tenorKeySet = tenorKeySet();

		int[] tenorJulianDateArray = new int[tenorKeySet.size()];

		for (String tenor : tenorKeySet) {
			tenorJulianDateArray[i++] = _spotDate.addTenor (tenor).julian();
		}

		return tenorJulianDateArray;
	}

	/**
	 * <i>DeGuillaumeRebonatoPogudinMarketYield</i> Constructor
	 * 
	 * @param currency Yield Currency
	 * @param spotDate Spot Date
	 * @param taxExemptMarketYieldTermStructure Tax-exempt <i>MarketYieldTermStructure</i>
	 * @param taxableMarketYieldTermStructure Taxable <i>MarketYieldTermStructure</i>
	 * @param escrowMarketYieldTermStructure Escrow (i.e., SGLS) <i>MarketYieldTermStructure</i>
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public DeGuillaumeRebonatoPogudinMarketSettings (
		final String currency,
		final JulianDate spotDate,
		final MarketYieldTermStructure taxExemptMarketYieldTermStructure,
		final MarketYieldTermStructure taxableMarketYieldTermStructure,
		final MarketYieldTermStructure escrowMarketYieldTermStructure)
		throws Exception
	{
		if (null == (_currency = currency) || _currency.isEmpty() ||
			null == (_spotDate = spotDate) ||
			null == (_taxExemptMarketYieldTermStructure = taxExemptMarketYieldTermStructure) ||
			null == (_taxableMarketYieldTermStructure = taxableMarketYieldTermStructure) ||
			null == (_escrowMarketYieldTermStructure = escrowMarketYieldTermStructure))
		{
			throw new Exception ("DeGuillaumeRebonatoPogudinMarketYield Constructor => Invalid Inputs");
		}

		Set<String> tenorKeySet = _taxExemptMarketYieldTermStructure.tenorKeySet();

		if (tenorKeySet.isEmpty() ||
			!tenorKeySet.equals (_taxableMarketYieldTermStructure.tenorKeySet()) ||
			!tenorKeySet.equals (_escrowMarketYieldTermStructure.tenorKeySet()))
		{
			throw new Exception ("DeGuillaumeRebonatoPogudinMarketYield Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Yield Currency
	 * 
	 * @return Yield Currency
	 */

	public String currency()
	{
		return _currency;
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return Spot Date
	 */

	public JulianDate spotDate()
	{
		return _spotDate;
	}

	/**
	 * Retrieve the Tax-exempt <i>MarketYieldTermStructure</i>
	 * 
	 * @return Tax-exempt <i>MarketYieldTermStructure</i>
	 */

	public MarketYieldTermStructure taxExemptMarketYieldTermStructure()
	{
		return _taxExemptMarketYieldTermStructure;
	}

	/**
	 * Retrieve the Taxable <i>MarketYieldTermStructure</i>
	 * 
	 * @return Taxable <i>MarketYieldTermStructure</i>
	 */

	public MarketYieldTermStructure taxableMarketYieldTermStructure()
	{
		return _taxableMarketYieldTermStructure;
	}

	/**
	 * Retrieve the Escrow (i.e., SGLS) <i>MarketYieldTermStructure</i>
	 * 
	 * @return Escrow (i.e., SGLS) <i>MarketYieldTermStructure</i>
	 */

	public MarketYieldTermStructure escrowMarketYieldTermStructure()
	{
		return _escrowMarketYieldTermStructure;
	}

	/**
	 * Retrieve the Set of Tenor Keys
	 * 
	 * @return Set of Tenor Keys
	 */

	public Set<String> tenorKeySet()
	{
		return _taxExemptMarketYieldTermStructure.tenorKeySet();
	}

	/**
	 * Retrieve the Spot Tax-exempt Govvie Curve
	 * 
	 * @return Spot Tax-exempt Govvie Curve
	 */

	public GovvieCurve spotTaxExemptGovvieCurve()
	{
		return ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
			"TAX_EXEMPT_" + _spotDate,
			_spotDate,
			TreasurySettingContainer.CurrencyBenchmarkCode (_currency),
			_currency,
			tenorJulianDateArray(),
			InitialYieldArray (_taxExemptMarketYieldTermStructure)
		);
	}

	/**
	 * Retrieve the Spot Taxable Govvie Curve
	 * 
	 * @return Spot Taxable Govvie Curve
	 */

	public GovvieCurve spotTaxableGovvieCurve()
	{
		return ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
			"TAXABLE_" + _spotDate,
			_spotDate,
			TreasurySettingContainer.CurrencyBenchmarkCode (_currency),
			_currency,
			tenorJulianDateArray(),
			InitialYieldArray (_taxableMarketYieldTermStructure)
		);
	}

	/**
	 * Retrieve the Spot Escrow Govvie Curve
	 * 
	 * @return Spot Escrow Govvie Curve
	 */

	public GovvieCurve spotEscrowGovvieCurve()
	{
		return ScenarioGovvieCurveBuilder.CubicPolynomialCurve (
			"TAXABLE_" + _spotDate,
			_spotDate,
			TreasurySettingContainer.CurrencyBenchmarkCode (_currency),
			_currency,
			tenorJulianDateArray(),
			InitialYieldArray (_escrowMarketYieldTermStructure)
		);
	}

	/**
	 * Generate the Bond Spot Curve Measures
	 * 
	 * @param bond Bond
	 * 
	 * @return Bond Spot Curve Measures
	 */

	public CurveMeasures spotCurveMeasures (
		final BondComponent bond)
	{
		int spotDateJulian = _spotDate.julian();

		ValuationParams valuationParams = ValuationParams.Spot (spotDateJulian);

		try {
			return new CurveMeasures (
				CurveMeasures.GovvieCurvePrice (bond, valuationParams, spotTaxExemptGovvieCurve()),
				CurveMeasures.GovvieCurvePrice (bond, valuationParams, spotTaxableGovvieCurve()),
				bond.accrued (spotDateJulian, null)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
