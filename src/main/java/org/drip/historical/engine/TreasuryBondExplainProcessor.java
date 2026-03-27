
package org.drip.historical.engine;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.daycount.ActActDCParams;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.historical.attribution.PositionMarketSnap;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.definition.Component;
import org.drip.product.govvie.TreasuryComponent;
import org.drip.state.govvie.GovvieCurve;
import org.drip.state.identifier.GovvieLabel;

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
 * <i>TreasuryBondExplainProcessor</i> contains the Functionality associated with the Horizon Analysis of the
 * 	Treasury Bond. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>TreasuryBondExplainProcessor</i> Constructor</li>
 * 		<li>Generate the Map of the Roll Down Market Quote Metrics</li>
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

public class TreasuryBondExplainProcessor
	extends HorizonChangeExplainProcessor
{

	/**
	 * <i>TreasuryBondExplainProcessor</i> Constructor
	 * 
	 * @param treasuryComponent The Treasury Component
	 * @param marketMeasure The Market Measure Name
	 * @param marketMeasureValue The Market Measure Value
	 * @param t1 First Date
	 * @param t2 Second Date
	 * @param t1CurveSurfaceQuoteContainer First Market Parameters
	 * @param t2CurveSurfaceQuoteContainer Second Market Parameters
	 * @param curveSurfaceQuoteContainerRollDownMap Map of the Roll Down Market Parameters
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public TreasuryBondExplainProcessor (
		final TreasuryComponent treasuryComponent,
		final String marketMeasure,
		final double marketMeasureValue,
		final JulianDate t1,
		final JulianDate t2,
		final CurveSurfaceQuoteContainer t1CurveSurfaceQuoteContainer,
		final CurveSurfaceQuoteContainer t2CurveSurfaceQuoteContainer,
		final CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> curveSurfaceQuoteContainerRollDownMap)
		throws Exception
	{
		super (
			treasuryComponent,
			0,
			marketMeasure,
			marketMeasureValue,
			t1,
			t2,
			t1CurveSurfaceQuoteContainer,
			t2CurveSurfaceQuoteContainer,
			curveSurfaceQuoteContainerRollDownMap
		);
	}

	/**
	 * Generate the Map of the Roll Down Market Quote Metrics
	 * 
	 * @return Map of the Roll Down Market Quote Metrics
	 */

	@Override public MarketMeasureRollDown rollDownMeasureMap()
	{
		Component component = component();

		MarketMeasureRollDown marketMeasureRollDown = null;

		int maturityDate = component.maturityDate().julian();

		GovvieLabel govvieLabel = component.govvieLabel();

		CaseInsensitiveHashMap<CurveSurfaceQuoteContainer> curveSurfaceQuoteContainerRollDownMap =
			curveSurfaceQuoteContainerRollDownMap();

		for (String rollDownTenor : curveSurfaceQuoteContainerRollDownMap.keySet()) {
			try {
				double marketMeasureRollDownValue = curveSurfaceQuoteContainerRollDownMap.get (
					rollDownTenor
				).govvieState (
					govvieLabel
				).yld (
					maturityDate
				);

				if ("Native".equalsIgnoreCase (rollDownTenor)) {
					marketMeasureRollDown = new MarketMeasureRollDown (marketMeasureRollDownValue);
				} else {
					marketMeasureRollDown.add (rollDownTenor, marketMeasureRollDownValue);
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return marketMeasureRollDown;
	}

	/**
	 * Generate and Snap Relevant Fields from the T1 Market Valuation Parameters
	 * 
	 * @return The T1 Market Parameters Valuation Snapshot
	 */

	@Override public PositionMarketSnap t1PositionMarketSnap()
	{
		TreasuryComponent treasuryComponent = (TreasuryComponent) component();

		JulianDate effectiveDate = treasuryComponent.effectiveDate();

		JulianDate maturityDate = treasuryComponent.maturityDate();

		GovvieLabel govvieLabel = treasuryComponent.govvieLabel();

		JulianDate t1 = t1();

		int valuationDate = t1.julian();

		String currency = treasuryComponent.currency();

		String accrualDC = treasuryComponent.accrualDC();

		MarketMeasureRollDown marketMeasureRollDown = rollDownMeasureMap();

		if (null == marketMeasureRollDown) {
			return null;
		}

		double rollDownInnate = marketMeasureRollDown.innate();

		double fixedCoupon = treasuryComponent.couponSetting().couponRate();

		CurveSurfaceQuoteContainer t1MarketParameters = t1MarketParameters();

		GovvieCurve govvieCurve = t1MarketParameters.govvieState (govvieLabel);

		ValuationParams valuationParams = ValuationParams.Spot (valuationDate);

		CaseInsensitiveHashMap<Double> horizonMetricMap = marketMeasureRollDown.horizonMetricMap();

		try {
			double yield = govvieCurve.yld (maturityDate.julian());

			double accrued = treasuryComponent.accrued (valuationDate, t1MarketParameters);

			double cleanPrice = treasuryComponent.priceFromYield (
				valuationParams,
				t1MarketParameters,
				null,
				yield
			);

			double yieldSensitivity = 10000. * treasuryComponent.modifiedDurationFromYield (
				valuationParams,
				t1MarketParameters,
				null,
				yield
			);

			double cumulativeCouponDCF = Convention.YearFraction (
				effectiveDate.julian(),
				valuationDate,
				accrualDC,
				false,
				ActActDCParams.FromFrequency (govvieCurve.freq()),
				currency
			);

			PositionMarketSnap positionMarketSnap = new PositionMarketSnap (t1, cleanPrice);

			if (!positionMarketSnap.setR1 ("Accrued", accrued) ||
				!positionMarketSnap.setC1 ("AccruedDC", accrualDC) ||
				!positionMarketSnap.setR1 ("CleanPrice", cleanPrice) ||
				!positionMarketSnap.setR1 ("CumulativeCouponAmount", cumulativeCouponDCF * fixedCoupon) ||
				!positionMarketSnap.setR1 ("CumulativeCouponDCF", cumulativeCouponDCF) ||
				!positionMarketSnap.setC1 ("Currency", currency) ||
				!positionMarketSnap.setR1 ("DirtyPrice", cleanPrice + accrued) ||
				!positionMarketSnap.setDate ("EffectiveDate", effectiveDate) ||
				!positionMarketSnap.setC1 ("FixedAccrualDayCount", accrualDC) ||
				!positionMarketSnap.setR1 ("FixedCoupon", fixedCoupon) ||
				!positionMarketSnap.setDate ("MaturityDate", maturityDate) ||
				!positionMarketSnap.setC1 ("MaturityTenor", treasuryComponent.tenor()) ||
				!positionMarketSnap.setR1 ("ModifiedDuration", yieldSensitivity) ||
				!positionMarketSnap.setR1 ("Yield", yield) ||
				!positionMarketSnap.setR1 ("YieldRollDown", rollDownInnate))
			{
				return null;
			}

			for (String rollDownTenor : horizonMetricMap.keySet()) {
				if (!positionMarketSnap.setR1 (
					"YieldRollDown" + rollDownTenor,
					horizonMetricMap.get (rollDownTenor)
				))
				{
					return null;
				}
			}

			if (!positionMarketSnap.addManifestMeasureSnap (
				"Yield",
				yield,
				-1. * yieldSensitivity,
				rollDownInnate
			))
			{
				return null;
			}

			return positionMarketSnap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Update the Fixings (if any) to the Second Market Parameters
	 * 
	 * @return TRUE - The Fixings were successfully updated to the Second Market Parameters
	 */

	@Override public boolean updateFixings()
	{
		return true;
	}

	/**
	 * Generate and Snap Relevant Fields from the T2 Market Valuation Parameters
	 * 
	 * @return The T2 Market Parameters Valuation Snapshot
	 */

	@Override public PositionMarketSnap t2PositionMarketSnap()
	{
		JulianDate t2 = t2();

		int valuationDate = t2.julian();

		TreasuryComponent treasuryComponent = (TreasuryComponent) component();

		ValuationParams valuationParams = ValuationParams.Spot (valuationDate);

		CurveSurfaceQuoteContainer secondMarketParameters = secondMarketParameters();

		GovvieCurve govvieCurve = secondMarketParameters.govvieState (treasuryComponent.govvieLabel());

		try {
			double yield = govvieCurve.yld (treasuryComponent.maturityDate().julian());

			double cumulativeCouponDCF = Convention.YearFraction (
				treasuryComponent.effectiveDate().julian(),
				valuationDate,
				treasuryComponent.accrualDC(),
				false,
				ActActDCParams.FromFrequency (govvieCurve.freq()),
				treasuryComponent.currency()
			);

			PositionMarketSnap positionMarketSnap = new PositionMarketSnap (
				t2,
				treasuryComponent.priceFromYield (valuationParams, secondMarketParameters, null, yield)
			);

			if (!positionMarketSnap.setR1 (
					"CumulativeCouponAmount",
					cumulativeCouponDCF * treasuryComponent.couponSetting().couponRate()
				) ||
				!positionMarketSnap.setR1 ("CumulativeCouponDCF", cumulativeCouponDCF) ||
				!positionMarketSnap.setR1 ("Yield", yield) ||
				!positionMarketSnap.addManifestMeasureSnap (
					"Yield",
					yield, -10000. * treasuryComponent.modifiedDurationFromYield (
						valuationParams,
						secondMarketParameters,
						null,
						yield
					),
					0.
				)
			)
			{
				return null;
			}

			return positionMarketSnap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Horizon Differential Metrics Map
	 * 
	 * @param t1PositionMarketSnap The First Position Market Snap
	 * @param t2PositionMarketSnap The Second Position Market Snap
	 * 
	 * @return The Horizon Differential Metrics Map
	 */

	@Override public CaseInsensitiveHashMap<Double> crossHorizonDifferentialMetrics (
		final PositionMarketSnap t1PositionMarketSnap,
		final PositionMarketSnap t2PositionMarketSnap)
	{
		if (null == t1PositionMarketSnap || null == t2PositionMarketSnap) {
			return null;
		}

		ActActDCParams actActDCParams =
			ActActDCParams.FromFrequency (((TreasuryComponent) component()).freq());

		CaseInsensitiveHashMap<Double> differentialMetricMap = new CaseInsensitiveHashMap<Double>();

		JulianDate effectiveDate = t1PositionMarketSnap.date ("EffectiveDate");

		String accrualDC = t1PositionMarketSnap.c1 ("AccruedDC");

		String calendar = t1PositionMarketSnap.c1 ("Currency");

		int effectiveDateJulian = effectiveDate.julian();

		try {
			differentialMetricMap.put (
				"CumulativeCouponAmount",
				t2PositionMarketSnap.r1 ("CumulativeCouponAmount") -
					t1PositionMarketSnap.r1 ("CumulativeCouponAmount")
			);

			differentialMetricMap.put (
				"CumulativeCouponDCF",
				t2PositionMarketSnap.r1 ("CumulativeCouponDCF") -
					t1PositionMarketSnap.r1 ("CumulativeCouponDCF")
			);

			differentialMetricMap.put (
				"CumulativeCouponDCF1M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDate.addTenor ("1M").julian(),
					accrualDC,
					false,
					actActDCParams,
					calendar
				)
			);

			differentialMetricMap.put (
				"CumulativeCouponDCF3M",
				Convention.YearFraction (
					effectiveDateJulian,
					effectiveDate.addTenor ("3M").julian(),
					accrualDC,
					false,
					actActDCParams,
					calendar
				)
			);

			return differentialMetricMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
