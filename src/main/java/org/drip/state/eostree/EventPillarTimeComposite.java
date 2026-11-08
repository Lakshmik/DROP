
package org.drip.state.eostree;

import java.util.List;
import java.util.TreeMap;

import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.JulianDate;
import org.drip.numerical.common.Array2D;
import org.drip.numerical.common.NumberUtil;
import org.drip.product.credit.BondComponent;
import org.drip.product.params.EmbeddedOptionSchedule;
import org.drip.product.params.NotionalSetting;
import org.drip.state.municipal.KalotayWilliamsFabozzi;
import org.drip.state.municipal.KalotayWilliamsFabozziPeriodState;

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
 * <i>EventPillarTimeComposite</i> holds the Composite of the Single Coupon/Exercise/Principal Tree Segments
 * 	built according to the Kalotay, Williams, and Fabozzi (1993) Tree-based Model for valuing bonds with
 * 	Embedded Options. The References are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/eostree/README.md">EOS Bond Tree Based Valuation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class EventPillarTimeComposite
{
	private boolean _isPut = false;
	private TreeMap<Double, Double> _exerciseTimeToPriceMap = null;
	private TreeMap<Double, Double> _timeToPrincipalPaydownMap = null;
	private TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> _timeToPeriodStateListMap = null;

	private static final boolean FromCoupons (
		final EventPillarTimeComposite couponExerciseComposite,
		final int spotDateJulian,
		final List<CompositePeriod> compositePeriodList,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		if (null == compositePeriodList || compositePeriodList.isEmpty()) {
			return true;
		}

		for (CompositePeriod compositePeriod : compositePeriodList) {
			int payDateJulian = compositePeriod.payDate();

			if (payDateJulian < spotDateJulian) {
				break;
			}

			double payTime = (payDateJulian - spotDateJulian) / 365.25;

			if (!couponExerciseComposite.containsTime (payTime) &&
				!couponExerciseComposite.addStateList (
					payTime,
					kalotayWilliamsFabozzi.bespokePeriodStateList (payTime)
				)
			)
			{
				return false;
			}
		}

		return true;
	}

	private static final boolean FromNotionalSchedule (
		final EventPillarTimeComposite couponExerciseComposite,
		final int spotDateJulian,
		final int maturityDateJulian,
		final NotionalSetting notionalSetting,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		Array2D outstandingFactorSchedule =
			null == notionalSetting ? null : notionalSetting.outstandingFactorSchedule();

		if (null == outstandingFactorSchedule) {
			double maturityTime = (maturityDateJulian - spotDateJulian) / 365.25;

			return couponExerciseComposite.containsTime (maturityTime) ||
				couponExerciseComposite.addStateList (
					maturityTime,
					kalotayWilliamsFabozzi.bespokePeriodStateList (maturityTime)
				);
		}

		double priorOutstandingFactor = 1.;

		double[] dateArray = outstandingFactorSchedule.x();

		double[] outstandingFactorArray = outstandingFactorSchedule.y();

		for (int dateIndex = 0; dateIndex < dateArray.length; ++dateIndex) {
			if (dateArray[dateIndex] < spotDateJulian) {
				break;
			}

			double notionalTime = (dateArray[dateIndex] - spotDateJulian) / 365.25;

			/* if (!couponExerciseComposite.containsTime (notionalTime) &&
				!couponExerciseComposite.addStateList (
					notionalTime,
					kalotayWilliamsFabozzi.bespokePeriodStateList (notionalTime)
				)
			)
			{
				return false;
			} */

			if (
				(
					priorOutstandingFactor != outstandingFactorArray[dateIndex] &&
					!couponExerciseComposite.containsTime (notionalTime) &&
					!couponExerciseComposite.addStateList (
						notionalTime,
						kalotayWilliamsFabozzi.bespokePeriodStateList (notionalTime)
					)
				) || !couponExerciseComposite.addPrincipalPaydown (
					notionalTime,
					priorOutstandingFactor - outstandingFactorArray[dateIndex]
				)
			)
			{
				return false;
			}

			priorOutstandingFactor = outstandingFactorArray[dateIndex];
		}

		return true;
	}

	private static final boolean FromEOS (
		final EventPillarTimeComposite couponExerciseComposite,
		final int spotDateJulian,
		final EmbeddedOptionSchedule embeddedOptionSchedule,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		if (null == embeddedOptionSchedule) {
			return true;
		}

		int[] exerciseDateArray = embeddedOptionSchedule.exerciseDates (spotDateJulian);

		double[] exerciseFactorArray = embeddedOptionSchedule.exerciseFactors (spotDateJulian);

		if (null == exerciseDateArray || null == exerciseFactorArray) {
			return true;
		}

		for (int exerciseIndex = 0; exerciseIndex < exerciseDateArray.length; ++exerciseIndex) {
			double exerciseTime = (exerciseDateArray[exerciseIndex] - spotDateJulian) / 365.25;

			if (
				(
					!couponExerciseComposite.containsTime (exerciseTime) &&
					!couponExerciseComposite.addStateList (
						exerciseTime,
						kalotayWilliamsFabozzi.bespokePeriodStateList (exerciseTime)
					)
				) || !couponExerciseComposite.addExercise (
					exerciseTime,
					exerciseFactorArray[exerciseIndex]
				)
			)
			{
				return false;
			}
		}

		return couponExerciseComposite.isPut (embeddedOptionSchedule.isPut());
	}

	/**
	 * Construct a Standard Instance of <i>EventPillarTimeComposite</i>
	 * 
	 * @param spotDate Spot Date
	 * @param bond Bond
	 * @param kalotayWilliamsFabozzi The <i>KalotayWilliamsFabozzi</i> Instance
	 * 
	 * @return Standard Instance of <i>EventPillarTimeComposite</i>
	 */

	public static final EventPillarTimeComposite Standard (
		final JulianDate spotDate,
		final BondComponent bond,
		final KalotayWilliamsFabozzi kalotayWilliamsFabozzi)
	{
		if (null == spotDate || null == bond || null == kalotayWilliamsFabozzi) {
			return null;
		}

		EventPillarTimeComposite couponExerciseComposite = new EventPillarTimeComposite();

		TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeToProjectedPeriodStateMap =
			kalotayWilliamsFabozzi.timeToProjectedPeriodStateMap();

		for (double endTime : timeToProjectedPeriodStateMap.keySet()) {
			if (!couponExerciseComposite.addStateList (endTime, timeToProjectedPeriodStateMap.get (endTime)))
			{
				return null;
			}
		}

		int spotDateJulian = spotDate.julian();

		if (!FromCoupons (
			couponExerciseComposite,
			spotDateJulian,
			bond.couponPeriods(),
			kalotayWilliamsFabozzi
		) || !FromNotionalSchedule (
			couponExerciseComposite,
			spotDateJulian,
			bond.maturityDate().julian(),
			bond.notionalSetting(),
			kalotayWilliamsFabozzi
		))
		{
			return null;
		}

		EmbeddedOptionSchedule embeddedOptionSchedule = bond.callSchedule();

		return FromEOS (
			couponExerciseComposite,
			spotDateJulian,
			null == embeddedOptionSchedule ? bond.putSchedule() : embeddedOptionSchedule,
			kalotayWilliamsFabozzi
		) ? couponExerciseComposite : null;
	}

	/**
	 * Empty <i>EventPillarTimeComposite</i> Constructor
	 */

	public EventPillarTimeComposite()
	{
		_exerciseTimeToPriceMap = new TreeMap<Double, Double>();

		_timeToPrincipalPaydownMap = new TreeMap<Double, Double>();

		_timeToPeriodStateListMap = new TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>>();
	}

	/**
	 * Retrieve the Map of Time to Exercise Price
	 * 
	 * @return Map of Time to Exercise Price
	 */

	public TreeMap<Double, Double> exerciseTimeToPriceMap()
	{
		return _exerciseTimeToPriceMap;
	}

	/**
	 * Retrieve the Map of Time to Principal Pay-down
	 * 
	 * @return Map of Time to Principal Pay-down
	 */

	public TreeMap<Double, Double> timeToPrincipalPaydownMap()
	{
		return _timeToPrincipalPaydownMap;
	}

	/**
	 * Retrieve the Map of Time to <i>KalotayWilliamsFabozziPeriodState</i> List
	 * 
	 * @return Map of Time to <i>KalotayWilliamsFabozziPeriodState</i> List
	 */

	public TreeMap<Double, List<KalotayWilliamsFabozziPeriodState>> timeToPeriodStateListMap()
	{
		return _timeToPeriodStateListMap;
	}

	/**
	 * Retrieve the Put Indicator
	 * 
	 * @return Put Indicator
	 */

	public boolean isPut()
	{
		return _isPut;
	}

	/**
	 * Add the List of <i>KalotayWilliamsFabozziPeriodState</i> Instances at the Time Node
	 * 
	 * @param time Time
	 * @param periodStateList List of <i>KalotayWilliamsFabozziPeriodState</i> Instances
	 * 
	 * @return TRUE - List of <i>KalotayWilliamsFabozziPeriodState</i> Instances at the Time Node
	 * 		   successfully added
	 */

	public boolean addStateList (
		final double time,
		final List<KalotayWilliamsFabozziPeriodState> periodStateList)
	{
		if (!NumberUtil.IsValid (time) || 0. > time ||
			null == periodStateList || 0 == periodStateList.size())
		{
			return false;
		}

		_timeToPeriodStateListMap.put (time, periodStateList);

		return true;
	}

	/**
	 * Add an Exercise Time/Price Node
	 * 
	 * @param time Time
	 * @param price Exercise Price
	 * 
	 * @return TRUE - Exercise Time/Price Node successfully added
	 */

	public boolean addExercise (
		final double time,
		final double price)
	{
		if (!NumberUtil.IsValid (time) || 0. > time || !NumberUtil.IsValid (price) || 0. > price) {
			return false;
		}

		_exerciseTimeToPriceMap.put (time, price);

		return true;
	}

	/**
	 * Add an Principal Pay-down Time/Price Node
	 * 
	 * @param time Time
	 * @param principalPaydown Principal Pay-down
	 * 
	 * @return TRUE - Principal Pay-down Node successfully added
	 */

	public boolean addPrincipalPaydown (
		final double time,
		final double principalPaydown)
	{
		if (!NumberUtil.IsValid (time) || 0. > time || !NumberUtil.IsValid (principalPaydown)) {
			return false;
		}

		_timeToPrincipalPaydownMap.put (time, principalPaydown);

		return true;
	}

	/**
	 * Set the Put Indicator
	 * 
	 * @param isPut Put Indicator
	 * 
	 * @return TRUE - The Put Indicator successfully set
	 */

	public boolean isPut (
		final boolean isPut)
	{
		_isPut = isPut;
		return true;
	}

	/**
	 * Indicate if the Time Key exists in Composite
	 * 
	 * @param time Time Key
	 * 
	 * @return TRUE - Time Key exists in Composite
	 */

	public boolean containsTime (
		final double time)
	{
		return NumberUtil.IsValid (time) && _timeToPeriodStateListMap.containsKey (time);
	}

	/**
	 * Indicate if the Time corresponds to an Exercise Node
	 * 
	 * @param time Time Key
	 * 
	 * @return TRUE - Time corresponds to an Exercise Node
	 */

	public boolean isExerciseNode (
		final double time)
	{
		return NumberUtil.IsValid (time) && _exerciseTimeToPriceMap.containsKey (time);
	}
}
