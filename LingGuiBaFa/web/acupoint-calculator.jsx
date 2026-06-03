import { useState } from "react";

// ══════════════════════════════════════════════
//  真太阳时计算
// ══════════════════════════════════════════════
// 均时差（Equation of Time），单位：分钟
// 使用Spencer公式，输入日期，返回分钟数
function equationOfTime(dateStr) {
  const d = new Date(dateStr);
  const start = new Date(d.getFullYear(), 0, 0);
  const diff = d - start;
  const dayOfYear = Math.floor(diff / 86400000);
  const B = (360 / 365) * (dayOfYear - 81) * (Math.PI / 180);
  // Spencer公式（分钟）
  const eot = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B);
  return eot; // 单位：分钟
}

// 计算真太阳时
// 输入：北京时间字符串"YYYY-MM-DDTHH:MM"，当地经度
// 输出：{ solarHour, solarMinute, solarDateStr, totalMinutesDiff, lonCorrection, eot }
function calcSolarTime(dtStr, longitude) {
  const [dateStr, timeStr] = dtStr.split("T");
  const [hh, mm] = timeStr.split(":").map(Number);
  const totalMinutesBJ = hh * 60 + mm;

  // 1. 经度修正（分钟）
  const lonCorrection = (longitude - 120) * 4;

  // 2. 均时差修正（分钟）
  const eot = equationOfTime(dateStr);

  // 3. 真太阳时总分钟
  const totalMinutes = totalMinutesBJ + lonCorrection + eot;

  // 处理跨日
  const totalMod = ((totalMinutes % 1440) + 1440) % 1440;
  const solarHour = Math.floor(totalMod / 60);
  const solarMinute = Math.round(totalMod % 60);

  // 真太阳时对应的日期（可能跨日）
  const base = new Date(dateStr + "T" + timeStr);
  const solarDate = new Date(base.getTime() + (lonCorrection + eot) * 60000);
  const p = n => String(n).padStart(2, "0");
  const solarDateStr = `${solarDate.getFullYear()}-${p(solarDate.getMonth()+1)}-${p(solarDate.getDate())}`;

  return {
    solarHour, solarMinute, solarDateStr,
    lonCorrection: Math.round(lonCorrection * 10) / 10,
    eot: Math.round(eot * 10) / 10,
    totalDiff: Math.round((lonCorrection + eot) * 10) / 10,
  };
}


// ══════════════════════════════════════════════
//  天干地支基础
// ══════════════════════════════════════════════
const STEMS   = ["甲","乙","丙","丁","戊","己","庚","辛","壬","癸"];
const BRANCHES= ["子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥"];
const BRANCH_MERIDIAN = ["胆","肝","肺","大肠","胃","脾","心","小肠","膀胱","肾","心包","三焦"];

// 日干支（以2024-01-01为基准，已验证）
function getDaySB(dateStr) {
  const base = new Date("2024-01-01");
  const d = new Date(dateStr);
  const diff = Math.round((d - base) / 86400000);
  return STEMS[((diff%10)+10)%10] + BRANCHES[((diff%12)+12)%12];
}

// 时干支
const HOUR_BASE = {甲:0,己:0,乙:2,庚:2,丙:4,辛:4,丁:6,壬:6,戊:8,癸:8};
function getHourSB(dayStem, hour) {
  const dh = hour===23 ? 0 : Math.floor((hour+1)/2)%12;
  return STEMS[(HOUR_BASE[dayStem]+dh)%10] + BRANCHES[dh];
}

// 当前时辰名
function getBranchLabel(hour) {
  const idx = hour===23 ? 0 : Math.floor((hour+1)/2)%12;
  return BRANCHES[idx]+"时";
}

// ══════════════════════════════════════════════
//  灵龟八法
// ══════════════════════════════════════════════
const LG_DS = {甲:10,己:10,乙:9,庚:9,丁:8,壬:8,戊:7,癸:7,丙:7,辛:7};
const LG_DB = {辰:10,戌:10,丑:10,未:10,申:9,酉:9,寅:8,卯:8,巳:7,午:7,亥:7,子:7};
const LG_HS = {甲:9,己:9,乙:8,庚:8,丙:7,辛:7,丁:6,壬:6,戊:5,癸:5};
const LG_HB = {子:9,午:9,丑:8,未:8,寅:7,申:7,卯:6,酉:6,辰:5,戌:5,巳:4,亥:4};
const LG_MAP= {1:"申脉",2:"照海",3:"外关",4:"足临泣",5:"照海",6:"公孙",7:"后溪",8:"内关",9:"列缺"};
const PAIR  = {申脉:"后溪",后溪:"申脉",照海:"列缺",列缺:"照海",外关:"足临泣",足临泣:"外关",内关:"公孙",公孙:"内关"};

function calcLingGui(daySB, hourSB) {
  const [ds,db,hs,hb] = [LG_DS[daySB[0]],LG_DB[daySB[1]],LG_HS[hourSB[0]],LG_HB[hourSB[1]]];
  const total = ds+db+hs+hb;
  const isYang = STEMS.indexOf(daySB[0])%2===0;
  const div = isYang?9:6;
  let rem = total%div; if(rem===0) rem=div;
  const main = LG_MAP[rem];
  return { main, pair: PAIR[main]||"—" };
}

// ══════════════════════════════════════════════
//  飞腾八法
// ══════════════════════════════════════════════
// 飞腾八法歌：壬甲->公孙(乾)，丙->内关(艮)，戊->足临泣(坎)，庚->外关(震)
//             辛->后溪(巽)，乙癸->申脉(坤)，己->列缺(离)，丁->照海(兑)
const FT_MAP = {甲:"公孙",壬:"公孙",丙:"内关",戊:"足临泣",庚:"外关",辛:"后溪",乙:"申脉",癸:"申脉",己:"列缺",丁:"照海"};
function calcFeiTeng(hourSB) {
  const main = FT_MAP[hourSB[0]];
  return { main, pair: PAIR[main]||"—" };
}

// ══════════════════════════════════════════════
//  子午流注纳子法（当令经取本穴+原穴）
// ══════════════════════════════════════════════
// [时支]: [当令经, 本穴, 本穴代号, 原穴, 原穴代号]
const NAZI_TABLE = {
  寅:["肺经",   "太渊","LU9", "太渊","LU9"],
  卯:["大肠经", "阳溪","LI5", "合谷","LI4"],
  辰:["胃经",   "足三里","ST36","冲阳","ST42"],
  巳:["脾经",   "太白","SP3", "太白","SP3"],
  午:["心经",   "少府","HT8", "神门","HT7"],
  未:["小肠经", "阳谷","SI5", "腕骨","SI4"],
  申:["膀胱经", "昆仑","BL60","京骨","BL64"],
  酉:["肾经",   "然谷","KI2", "太溪","KI3"],
  戌:["心包经", "劳宫","PC8", "大陵","PC7"],
  亥:["三焦经", "支沟","TE6", "阳池","TE4"],
  子:["胆经",   "足临泣","GB41","丘墟","GB40"],
  丑:["肝经",   "大敦","LR1", "太冲","LR3"],
};
function calcNaZi(hourSB) {
  const branch = hourSB[1];
  const row = NAZI_TABLE[branch];
  const samePoint = row[1] === row[3];
  return { meridian: row[0], ben: row[1], benCode: row[2], yuan: row[3], yuanCode: row[4], samePoint };
}

// ══════════════════════════════════════════════
//  子午流注纳甲法（按歌诀完整60时辰查找表）
// ══════════════════════════════════════════════
// 格式：日干 -> { "时干支": [{name,code,type,meridian}, ...] }
const NAJIA_TABLE = {
  甲: {
    甲戌:[{name:"窍阴",code:"GB44",type:"井",m:"胆经"}],
    丙子:[{name:"前谷",code:"SI2",type:"荥",m:"小肠经"}],
    戊寅:[{name:"陷谷",code:"ST43",type:"俞",m:"胃经"},{name:"丘墟",code:"GB40",type:"原",m:"胆经"}],
    庚辰:[{name:"阳溪",code:"LI5",type:"经",m:"大肠经"}],
    壬午:[{name:"委中",code:"BL40",type:"合",m:"膀胱经"}],
    甲申:[{name:"液门",code:"TE2",type:"荥",m:"三焦经"}],
  },
  乙: {
    乙酉:[{name:"大敦",code:"LR1",type:"井",m:"肝经"}],
    丁亥:[{name:"少府",code:"HT8",type:"荥",m:"心经"}],
    己丑:[{name:"太白",code:"SP3",type:"俞",m:"脾经"},{name:"太冲",code:"LR3",type:"原",m:"肝经"}],
    辛卯:[{name:"经渠",code:"LU8",type:"经",m:"肺经"}],
    癸巳:[{name:"阴谷",code:"KI10",type:"合",m:"肾经"}],
    乙未:[{name:"劳宫",code:"PC8",type:"荥",m:"心包经"}],
  },
  丙: {
    丙申:[{name:"少泽",code:"SI1",type:"井",m:"小肠经"}],
    戊戌:[{name:"内庭",code:"ST44",type:"荥",m:"胃经"}],
    庚子:[{name:"三间",code:"LI3",type:"俞",m:"大肠经"},{name:"腕骨",code:"SI4",type:"原",m:"小肠经"}],
    壬寅:[{name:"昆仑",code:"BL60",type:"经",m:"膀胱经"}],
    甲辰:[{name:"阳陵泉",code:"GB34",type:"合",m:"胆经"}],
    丙午:[{name:"中渚",code:"TE3",type:"俞",m:"三焦经"}],
  },
  丁: {
    丁未:[{name:"少冲",code:"HT9",type:"井",m:"心经"}],
    己酉:[{name:"大都",code:"SP2",type:"荥",m:"脾经"}],
    辛亥:[{name:"太渊",code:"LU9",type:"俞",m:"肺经"},{name:"神门",code:"HT7",type:"原",m:"心经"}],
    癸丑:[{name:"复溜",code:"KI7",type:"经",m:"肾经"}],
    乙卯:[{name:"曲泉",code:"LR8",type:"合",m:"肝经"}],
    丁巳:[{name:"大陵",code:"PC7",type:"俞",m:"心包经"}],
  },
  戊: {
    戊午:[{name:"厉兑",code:"ST45",type:"井",m:"胃经"}],
    庚申:[{name:"二间",code:"LI2",type:"荥",m:"大肠经"}],
    壬戌:[{name:"束骨",code:"BL65",type:"俞",m:"膀胱经"},{name:"冲阳",code:"ST42",type:"原",m:"胃经"}],
    甲子:[{name:"阳辅",code:"GB38",type:"经",m:"胆经"}],
    丙寅:[{name:"小海",code:"SI8",type:"合",m:"小肠经"}],
    戊辰:[{name:"支沟",code:"TE6",type:"经",m:"三焦经"}],
  },
  己: {
    己巳:[{name:"隐白",code:"SP1",type:"井",m:"脾经"}],
    辛未:[{name:"鱼际",code:"LU10",type:"荥",m:"肺经"}],
    癸酉:[{name:"太溪",code:"KI3",type:"俞",m:"肾经"},{name:"太白",code:"SP3",type:"原",m:"脾经"}],
    乙亥:[{name:"中封",code:"LR4",type:"经",m:"肝经"}],
    丁丑:[{name:"少海",code:"HT3",type:"合",m:"心经"}],
    己卯:[{name:"间使",code:"PC5",type:"经",m:"心包经"}],
  },
  庚: {
    庚辰:[{name:"商阳",code:"LI1",type:"井",m:"大肠经"}],
    壬午:[{name:"通谷",code:"BL66",type:"荥",m:"膀胱经"}],
    甲申:[{name:"足临泣",code:"GB41",type:"俞",m:"胆经"},{name:"合谷",code:"LI4",type:"原",m:"大肠经"}],
    丙戌:[{name:"阳谷",code:"SI5",type:"经",m:"小肠经"}],
    戊子:[{name:"足三里",code:"ST36",type:"合",m:"胃经"}],
    庚寅:[{name:"天井",code:"TE10",type:"合",m:"三焦经"}],
  },
  辛: {
    辛卯:[{name:"少商",code:"LU11",type:"井",m:"肺经"}],
    癸巳:[{name:"然谷",code:"KI2",type:"荥",m:"肾经"}],
    乙未:[{name:"太冲",code:"LR3",type:"俞",m:"肝经"},{name:"太渊",code:"LU9",type:"原",m:"肺经"}],
    丁酉:[{name:"灵道",code:"HT4",type:"经",m:"心经"}],
    己亥:[{name:"阴陵泉",code:"SP9",type:"合",m:"脾经"}],
    辛丑:[{name:"曲泽",code:"PC3",type:"合",m:"心包经"}],
  },
  壬: {
    壬寅:[{name:"至阴",code:"BL67",type:"井",m:"膀胱经"}],
    甲辰:[{name:"侠溪",code:"GB43",type:"荥",m:"胆经"}],
    丙午:[{name:"后溪",code:"SI3",type:"俞",m:"小肠经"},{name:"京骨",code:"BL64",type:"原",m:"膀胱经"},{name:"阳池",code:"TE4",type:"原",m:"三焦经"}],
    戊申:[{name:"解溪",code:"ST41",type:"经",m:"胃经"}],
    庚戌:[{name:"曲池",code:"LI11",type:"合",m:"大肠经"}],
    壬子:[{name:"关冲",code:"TE1",type:"井",m:"三焦经"}],
  },
  癸: {
    癸亥:[{name:"涌泉",code:"KI1",type:"井",m:"肾经"}],
    乙丑:[{name:"行间",code:"LR2",type:"荥",m:"肝经"}],
    丁卯:[{name:"神门",code:"HT7",type:"俞",m:"心经"},{name:"太溪",code:"KI3",type:"原",m:"肾经"},{name:"大陵",code:"PC7",type:"原",m:"心包经"}],
    己巳:[{name:"商丘",code:"SP5",type:"经",m:"脾经"}],
    辛未:[{name:"尺泽",code:"LU5",type:"合",m:"肺经"}],
    癸酉:[{name:"中冲",code:"PC9",type:"井",m:"心包经"}],
  },
};

function calcNaJia(daySB, hourSB) {
  const dayStem = daySB[0];
  const hourSBKey = hourSB; // 时干支两字作为key
  const dayTable = NAJIA_TABLE[dayStem];
  if (!dayTable) return null;
  const points = dayTable[hourSBKey];
  if (!points) return { points: null };
  return { points };
}

// ══════════════════════════════════════════════
//  穴位信息（所属经络）
// ══════════════════════════════════════════════
const POINT_META = {
  "申脉":{m:"膀胱经",e:"阳跷脉"},  "照海":{m:"肾经",e:"阴跷脉"},
  "外关":{m:"三焦经",e:"阳维脉"}, "足临泣":{m:"胆经",e:"带脉"},
  "公孙":{m:"脾经",e:"冲脉"},    "后溪":{m:"小肠经",e:"督脉"},
  "内关":{m:"心包经",e:"阴维脉"}, "列缺":{m:"肺经",e:"任脉"},
};

// ══════════════════════════════════════════════
//  主组件
// ══════════════════════════════════════════════
function toLocalDT(d) {
  const p = n=>String(n).padStart(2,"0");
  return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}`;
}

export default function App() {
  const [dt, setDt] = useState(toLocalDT(new Date()));
  const [results, setResults] = useState(null);
  const [key, setKey] = useState(0);
  const [longitude, setLongitude] = useState(116.4); // 默认北京
  const [locName, setLocName] = useState("北京");
  const [locating, setLocating] = useState(false);
  const [useSolar, setUseSolar] = useState(false);
  const [solarInfo, setSolarInfo] = useState(null);

  function getLocation() {
    if (!navigator.geolocation) return;
    setLocating(true);
    navigator.geolocation.getCurrentPosition(
      pos => {
        const lon = Math.round(pos.coords.longitude * 100) / 100;
        setLongitude(lon);
        setLocName(`东经 ${lon}°`);
        setLocating(false);
        setResults(null);
      },
      () => setLocating(false)
    );
  }

  function calculate() {
    const [dateStr, timeStr] = dt.split("T");
    let calcDt = dt;
    let solar = null;

    if (useSolar) {
      solar = calcSolarTime(dt, longitude);
      const p = n => String(n).padStart(2, "0");
      calcDt = `${solar.solarDateStr}T${p(solar.solarHour)}:${p(solar.solarMinute)}`;
    }

    const [calcDate, calcTime] = calcDt.split("T");
    const hour = parseInt(calcTime.split(":")[0]);
    const daySB = getDaySB(calcDate);
    const hourSB = getHourSB(daySB[0], hour);
    const branchLabel = getBranchLabel(hour);
    setSolarInfo(solar);
    setResults({
      daySB, hourSB, branchLabel,
      lingGui: calcLingGui(daySB, hourSB),
      feiTeng: calcFeiTeng(hourSB),
      naZi:    calcNaZi(hourSB),
      naJia:   calcNaJia(daySB, hourSB),
    });
    setKey(k=>k+1);
  }

  function setNow() { setDt(toLocalDT(new Date())); setResults(null); setSolarInfo(null); }

  return (
    <div style={s.root}>
      <div style={s.bg}/>
      <div style={s.wrap}>

        {/* 顶部标题 */}
        <header style={s.header}>
          <div style={s.headerGlyph}>針</div>
          <h1 style={s.title}>流注開穴</h1>
          <p style={s.sub}>子午流注 · 靈龜八法 · 飛騰八法</p>
        </header>

        {/* 时间输入 */}
        <div style={s.card}>
          <div style={s.inputRow}>
            <input
              type="datetime-local"
              value={dt}
              onChange={e=>{setDt(e.target.value);setResults(null);}}
              style={s.input}
            />
          </div>

          {/* 真太阳时开关 */}
          <div style={s.solarRow}>
            <label style={s.solarToggle}>
              <input type="checkbox" checked={useSolar} onChange={e=>setUseSolar(e.target.checked)} style={{marginRight:6}}/>
              启用真太阳时修正
            </label>
          </div>

          {/* 经度设置（启用时显示） */}
          {useSolar && (
            <div style={s.lonRow}>
              <div style={s.lonLeft}>
                <span style={s.lonLabel}>当地经度</span>
                <input
                  type="number"
                  value={longitude}
                  step="0.1"
                  min="70"
                  max="135"
                  onChange={e=>setLongitude(parseFloat(e.target.value)||116.4)}
                  style={s.lonInput}
                />
                <span style={s.lonUnit}>° E</span>
              </div>
              <button style={s.btnLoc} onClick={getLocation} disabled={locating}>
                {locating ? "定位中…" : "📍 自动定位"}
              </button>
            </div>
          )}

          <div style={s.btnRow}>
            <button style={s.btn2} onClick={setNow}>⟳ 当前时间</button>
            <button style={s.btn1} onClick={calculate}>推算开穴</button>
          </div>
        </div>

        {/* 真太阳时结果提示 */}
        {solarInfo && (
          <div style={s.solarCard}>
            <div style={s.solarTitle}>真太阳时修正</div>
            <div style={s.solarDetails}>
              <span>经度修正 {solarInfo.lonCorrection > 0 ? "+" : ""}{solarInfo.lonCorrection} 分</span>
              <span style={s.solarSep}>·</span>
              <span>均时差 {solarInfo.eot > 0 ? "+" : ""}{solarInfo.eot} 分</span>
              <span style={s.solarSep}>·</span>
              <span style={{color: solarInfo.totalDiff >= 0 ? "#3d6b5e" : "#7a3d4a", fontWeight:600}}>
                合计 {solarInfo.totalDiff > 0 ? "+" : ""}{solarInfo.totalDiff} 分
              </span>
            </div>
            <div style={s.solarTime}>
              真太阳时：{String(solarInfo.solarHour).padStart(2,"0")}:{String(solarInfo.solarMinute).padStart(2,"0")}
            </div>
          </div>
        )}

        {/* 结果 */}
        {results && (
          <div key={key} style={s.results}>
            {/* 干支显示 */}
            <div style={s.sbBanner}>
              <SBPill label="日干支" val={results.daySB}/>
              <div style={s.sbDot}>·</div>
              <SBPill label="时干支" val={results.hourSB}/>
              <div style={s.sbDot}>·</div>
              <SBPill label="时辰" val={results.branchLabel}/>
            </div>

            {/* 四法卡片 */}
            <MethodCard
              title="灵龟八法" color="#7c5c2e"
              badge="奇经纳卦"
              content={<PairPoints main={results.lingGui.main} pair={results.lingGui.pair} showMeta/>}
            />
            <MethodCard
              title="飞腾八法" color="#3d6b5e"
              badge=""
              content={<PairPoints main={results.feiTeng.main} pair={results.feiTeng.pair} showMeta/>}
            />
            <MethodCard
              title="子午流注纳子法" color="#4a5e8a"
              badge={results.naZi.meridian+"当令"}
              content={<NaZiDisplay data={results.naZi}/>}
            />
            <MethodCard
              title="子午流注纳甲法" color="#7a3d4a"
              badge={results.naJia?.points ? results.naJia.points[0].m : ""}
              content={<NaJiaDisplay data={results.naJia}/>}
            />
          </div>
        )}

        <div style={s.foot}>按时取穴 · 因时制宜</div>
      </div>

      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@300;400;600;700&family=Ma+Shan+Zheng&display=swap');
        *{box-sizing:border-box;margin:0;padding:0;}
        html{-webkit-text-size-adjust:100%;text-size-adjust:100%;}
        body{-webkit-tap-highlight-color:transparent;}
        @keyframes rise{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:translateY(0)}}
        input[type=datetime-local]{-webkit-appearance:none;appearance:none;}
        input[type=datetime-local]::-webkit-calendar-picker-indicator{filter:invert(.5);cursor:pointer;padding:2px;}
        input[type=number]{-webkit-appearance:none;appearance:none;-moz-appearance:textfield;}
        input[type=number]::-webkit-inner-spin-button,
        input[type=number]::-webkit-outer-spin-button{-webkit-appearance:none;margin:0;}
        button{-webkit-tap-highlight-color:transparent;touch-action:manipulation;}
        button:active{opacity:0.75;}
        input:focus{outline:none;border-color:#7c5c2e !important;}
      `}</style>
    </div>
  );
}

// 干支胶囊
function SBPill({label,val}) {
  return (
    <div style={s.sbPill}>
      <div style={s.sbPillLabel}>{label}</div>
      <div style={s.sbPillVal}>{val}</div>
    </div>
  );
}

// 方法卡
function MethodCard({title,color,badge,content}) {
  return (
    <div style={{...s.mCard, borderLeftColor:color, animation:"rise .45s ease both"}}>
      <div style={s.mHead}>
        <span style={{...s.mTitle, color}}>{title}</span>
        {badge && <span style={{...s.mBadge, background:color+"18", color}}>{badge}</span>}
      </div>
      {content}
    </div>
  );
}

// 灵龟/飞腾：主穴+配穴
function PairPoints({main, pair, showMeta}) {
  const mainMeta = POINT_META[main];
  const pairMeta = POINT_META[pair];
  return (
    <div style={s.pairRow}>
      <PointChip name={main} meta={mainMeta} label="开穴"/>
      {pair && pair!=="—" && <>
        <div style={s.pairPlus}>+</div>
        <PointChip name={pair} meta={pairMeta} label="配穴"/>
      </>}
    </div>
  );
}

function PointChip({name, meta, label}) {
  return (
    <div style={s.chip}>
      <div style={s.chipLabel}>{label}</div>
      <div style={s.chipName}>{name}</div>
      {meta && <div style={s.chipMeta}>{meta.m} · 通{meta.e}</div>}
    </div>
  );
}

// 纳子法
function NaZiDisplay({data}) {
  return (
    <div style={s.pairRow}>
      <div style={s.chip}>
        <div style={s.chipLabel}>本穴</div>
        <div style={s.chipName}>{data.ben}</div>
        <div style={s.chipMeta}>{data.meridian} · {data.benCode}</div>
      </div>
      {!data.samePoint && <>
        <div style={s.pairPlus}>+</div>
        <div style={s.chip}>
          <div style={s.chipLabel}>原穴</div>
          <div style={s.chipName}>{data.yuan}</div>
          <div style={s.chipMeta}>{data.meridian} · {data.yuanCode}</div>
        </div>
      </>}
    </div>
  );
}

// 纳甲法
function NaJiaDisplay({data}) {
  if(!data) return <div style={s.none}>此时辰闭穴（无）</div>;
  if(!data.points) return <div style={s.none}>此时辰闭穴（无）</div>;
  return (
    <div style={s.pairRow}>
      {data.points.map((p,i)=>(
        <div key={i} style={s.chip}>
          <div style={s.chipLabel}>{p.type}</div>
          <div style={s.chipName}>{p.name}</div>
          <div style={s.chipMeta}>{p.m} · {p.code}</div>
        </div>
      ))}
    </div>
  );
}

// ══════════════════════════════════════════════ styles
const BG="#f5f0e8", PAPER="#fffef9", INK="#261e12", MUT="#8c7660", BORDER="#e0d4be";
const s = {
  // ── 布局 ──
  root:{minHeight:"100vh",minHeight:"100dvh",background:BG,fontFamily:"'Noto Serif SC',serif",color:INK,position:"relative",overflowX:"hidden"},
  bg:{position:"fixed",inset:0,pointerEvents:"none",backgroundImage:"radial-gradient(ellipse at 15% 10%,#c9a84c0d,transparent 55%),radial-gradient(ellipse at 85% 90%,#8b45130d,transparent 55%)"},
  wrap:{position:"relative",zIndex:1,width:"100%",maxWidth:520,margin:"0 auto",padding:"20px 14px 72px",display:"flex",flexDirection:"column",gap:14},

  // ── 顶部标题 ──
  header:{textAlign:"center",paddingTop:4,marginBottom:0,position:"relative"},
  headerGlyph:{position:"absolute",top:-12,left:"50%",transform:"translateX(-50%)",fontSize:80,fontFamily:"'Ma Shan Zheng',serif",color:"#c9a84c0e",lineHeight:1,userSelect:"none",pointerEvents:"none"},
  title:{fontSize:26,fontWeight:700,letterSpacing:6,fontFamily:"'Ma Shan Zheng',serif",position:"relative"},
  sub:{fontSize:11,color:MUT,letterSpacing:2,marginTop:3},

  // ── 输入卡片 ──
  card:{background:PAPER,border:`1px solid ${BORDER}`,borderRadius:14,padding:"14px 16px",boxShadow:"0 2px 12px #26161206"},
  inputRow:{marginBottom:10},
  // datetime-local 在手机上字体要够大防止缩放
  input:{width:"100%",background:BG,border:`1px solid ${BORDER}`,borderRadius:10,padding:"12px 14px",fontSize:16,color:INK,fontFamily:"'Noto Serif SC',serif",outline:"none",WebkitAppearance:"none"},
  btnRow:{display:"flex",gap:10,marginTop:12},
  // 按钮高度 48px，符合触摸目标规范
  btn2:{flex:1,height:48,background:"transparent",border:`1px solid ${BORDER}`,borderRadius:10,fontSize:13,color:MUT,cursor:"pointer",fontFamily:"'Noto Serif SC',serif",letterSpacing:1},
  btn1:{flex:2,height:48,background:"#7c5c2e",border:"none",borderRadius:10,fontSize:14,color:"#fff",cursor:"pointer",fontFamily:"'Noto Serif SC',serif",letterSpacing:3,fontWeight:600,boxShadow:"0 3px 14px #7c5c2e44"},

  // ── 真太阳时控件 ──
  solarRow:{display:"flex",alignItems:"center",marginTop:12},
  solarToggle:{fontSize:14,color:MUT,cursor:"pointer",display:"flex",alignItems:"center",gap:8,letterSpacing:1,userSelect:"none"},
  lonRow:{display:"flex",alignItems:"center",justifyContent:"space-between",gap:8,marginTop:10,padding:"10px 12px",background:BG,borderRadius:10},
  lonLeft:{display:"flex",alignItems:"center",gap:6,flexWrap:"wrap"},
  lonLabel:{fontSize:12,color:MUT,letterSpacing:1},
  // 数字输入框在手机上也要 16px 防缩放
  lonInput:{width:72,background:PAPER,border:`1px solid ${BORDER}`,borderRadius:8,padding:"7px 8px",fontSize:16,color:INK,fontFamily:"'Noto Serif SC',serif",outline:"none",textAlign:"center"},
  lonUnit:{fontSize:13,color:MUT},
  // 定位按钮高度 40px
  btnLoc:{height:40,padding:"0 14px",background:"transparent",border:`1px solid ${BORDER}`,borderRadius:8,fontSize:12,color:MUT,cursor:"pointer",fontFamily:"'Noto Serif SC',serif",letterSpacing:1,whiteSpace:"nowrap"},

  // ── 真太阳时结果卡 ──
  solarCard:{background:"#eef6f3",border:"1px solid #b8d8ce",borderRadius:12,padding:"12px 16px"},
  solarTitle:{fontSize:11,color:"#3d6b5e",letterSpacing:2,marginBottom:8},
  solarDetails:{display:"flex",alignItems:"center",flexWrap:"wrap",gap:"4px 8px",fontSize:12,color:MUT,marginBottom:8,lineHeight:1.8},
  solarSep:{color:BORDER},
  solarTime:{fontSize:22,fontWeight:700,fontFamily:"'Ma Shan Zheng',serif",letterSpacing:4,color:"#3d6b5e"},

  // ── 干支 Banner ──
  results:{display:"flex",flexDirection:"column",gap:12},
  sbBanner:{display:"flex",alignItems:"center",justifyContent:"center",gap:6,padding:"10px 0"},
  sbPill:{textAlign:"center"},
  sbPillLabel:{fontSize:10,color:MUT,letterSpacing:2,marginBottom:2},
  sbPillVal:{fontSize:20,fontWeight:700,letterSpacing:3,fontFamily:"'Ma Shan Zheng',serif"},
  sbDot:{fontSize:16,color:BORDER,alignSelf:"flex-end",paddingBottom:2},

  // ── 方法卡片 ──
  mCard:{background:PAPER,border:`1px solid ${BORDER}`,borderLeft:"3px solid",borderRadius:12,padding:"14px 14px",boxShadow:"0 1px 8px #26161204"},
  mHead:{display:"flex",alignItems:"center",gap:8,marginBottom:12,flexWrap:"wrap"},
  mTitle:{fontSize:13,fontWeight:600,letterSpacing:2},
  mBadge:{fontSize:10,padding:"2px 8px",borderRadius:20,letterSpacing:1},

  // ── 穴位格子：手机宽度自适应 ──
  pairRow:{display:"flex",alignItems:"stretch",gap:8,flexWrap:"wrap"},
  pairPlus:{fontSize:18,color:BORDER,fontWeight:300,display:"flex",alignItems:"center"},
  // minWidth 让每格在小屏幕也撑开，但不超过容器
  chip:{flex:"1 1 80px",minWidth:80,background:BG,borderRadius:10,padding:"10px 8px",textAlign:"center"},
  chipLabel:{fontSize:10,color:MUT,letterSpacing:2,marginBottom:4},
  chipName:{fontSize:19,fontWeight:700,letterSpacing:2,fontFamily:"'Ma Shan Zheng',serif",marginBottom:3},
  chipMeta:{fontSize:10,color:MUT,letterSpacing:0.5,lineHeight:1.5},
  none:{fontSize:13,color:MUT,padding:"6px 0",letterSpacing:1},

  foot:{textAlign:"center",fontSize:10,color:BORDER,letterSpacing:3,marginTop:4},
};
