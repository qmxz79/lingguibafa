from datetime import datetime, date
import tkinter as tk
from tkinter import ttk
from tkcalendar import Calendar

class ChineseCalendar:
    # 天干
    HEAVENLY_STEMS = ["甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"]
    # 地支
    EARTHLY_BRANCHES = ["子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"]

    @staticmethod
    def get_day_stem_branch(input_date=None):
        """计算指定日期的日干支"""
        if input_date is None:
            input_date = date.today()
        elif isinstance(input_date, str):
            input_date = datetime.strptime(input_date, "%Y-%m-%d").date()
        
        base_date = date(2024, 1, 1)
        days_diff = (input_date - base_date).days
        
        stem_index = days_diff % 10
        branch_index = days_diff % 12
        
        return f"{ChineseCalendar.HEAVENLY_STEMS[stem_index]}{ChineseCalendar.EARTHLY_BRANCHES[branch_index]}"

    @staticmethod
    def get_hour_stem_branch(day_stem, hour):
        """计算时辰干支"""
        # 将24小时制转换为12时辰
        if hour == 23 or hour == 0:
            double_hour = 0  # 子时
        else:
            double_hour = ((hour + 1) % 24) // 2
        
        # 根据日干确定子时的天干
        day_stem_index = ChineseCalendar.HEAVENLY_STEMS.index(day_stem)
        # 甲己日起甲，乙庚日起丙，丙辛日起戊，丁壬日起庚，戊癸日起壬
        base_stems = {"甲": 0, "己": 0, "乙": 2, "庚": 2, "丙": 4, "辛": 4, "丁": 6, "壬": 6, "戊": 8, "癸": 8}
        zi_stem_index = base_stems[day_stem]
        
        # 计算时辰的天干和地支索引
        hour_stem_index = (zi_stem_index + double_hour) % 10
        hour_branch_index = double_hour
        
        return ChineseCalendar.HEAVENLY_STEMS[hour_stem_index] + ChineseCalendar.EARTHLY_BRANCHES[hour_branch_index]

class LingGuiBaFa:
    # 日干支对应数字
    DAY_STEM_NUMBERS = {
        "甲": 10, "己": 10, "乙": 9, "庚": 9, "丁": 8, "壬": 8,
        "戊": 7, "癸": 7, "丙": 7, "辛": 7
    }
    
    DAY_BRANCH_NUMBERS = {
        "辰": 10, "戌": 10, "丑": 10, "未": 10,
        "申": 9, "酉": 9,
        "寅": 8, "卯": 8,
        "巳": 7, "午": 7, "亥": 7, "子": 7
    }
    
    # 时干支对应数字
    HOUR_STEM_NUMBERS = {
        "甲": 9, "己": 9, "乙": 8, "庚": 8,
        "丙": 7, "辛": 7, "丁": 6, "壬": 6,
        "戊": 5, "癸": 5
    }
    
    HOUR_BRANCH_NUMBERS = {
        "子": 9, "午": 9, "丑": 8, "未": 8,
        "寅": 7, "申": 7, "卯": 6, "酉": 6,
        "辰": 5, "戌": 5, "巳": 4, "亥": 4
    }
    
    # 穴位对应表
    ACUPOINT_MAP = {
        1: "申脉", 2: "照海", 3: "外关", 4: "临泣",
        5: "照海", 6: "公孙", 7: "后溪", 8: "内关", 9: "列缺"
    }

    @staticmethod
    def calculate_acupoint(day_sb, hour_sb):
        """计算灵龟八法开穴"""
        day_stem, day_branch = day_sb[0], day_sb[1]
        hour_stem, hour_branch = hour_sb[0], hour_sb[1]
        
        # 获取对应的数字
        day_stem_num = LingGuiBaFa.DAY_STEM_NUMBERS[day_stem]
        day_branch_num = LingGuiBaFa.DAY_BRANCH_NUMBERS[day_branch]
        hour_stem_num = LingGuiBaFa.HOUR_STEM_NUMBERS[hour_stem]
        hour_branch_num = LingGuiBaFa.HOUR_BRANCH_NUMBERS[hour_branch]
        
        # 计算总和
        total = day_stem_num + day_branch_num + hour_stem_num + hour_branch_num
        
        # 判断阴阳日
        day_stem_index = ChineseCalendar.HEAVENLY_STEMS.index(day_stem)
        is_yang = day_stem_index % 2 == 0  # 天干索引为偶数是阳干
        
        # 计算余数
        divisor = 9 if is_yang else 6
        remainder = total % divisor
        if remainder == 0:
            remainder = divisor
            
        return {
            "日干数": day_stem_num,
            "日支数": day_branch_num,
            "时干数": hour_stem_num,
            "时支数": hour_branch_num,
            "总和": total,
            "阴阳": "阳" if is_yang else "阴",
            "除数": divisor,
            "余数": remainder,
            "穴位": LingGuiBaFa.ACUPOINT_MAP[remainder]
        }

class Calculator:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("灵龟八法计算器")
        self.root.geometry("400x600")
        
        # 创建日期选择器
        self.cal = Calendar(self.root, selectmode='day', date_pattern='y-mm-dd')
        self.cal.pack(pady=10)
        
        # 时间选择框架
        time_frame = ttk.Frame(self.root)
        time_frame.pack(pady=10)
        
        ttk.Label(time_frame, text="时:").pack(side=tk.LEFT)
        self.hour_var = tk.StringVar(value="0")
        self.hour_spin = ttk.Spinbox(time_frame, from_=0, to=23, width=5, textvariable=self.hour_var)
        self.hour_spin.pack(side=tk.LEFT, padx=5)
        
        ttk.Label(time_frame, text="分:").pack(side=tk.LEFT)
        self.minute_var = tk.StringVar(value="0")
        self.minute_spin = ttk.Spinbox(time_frame, from_=0, to=59, width=5, textvariable=self.minute_var)
        self.minute_spin.pack(side=tk.LEFT, padx=5)
        
        # 按钮框架
        button_frame = ttk.Frame(self.root)
        button_frame.pack(pady=10)
        
        # 当前时间按钮
        ttk.Button(button_frame, text="设置为当前时间", command=self.set_current_time).pack(side=tk.LEFT, padx=5)
        
        # 计算按钮
        ttk.Button(button_frame, text="计算干支", command=self.calculate_stem_branch).pack(side=tk.LEFT, padx=5)
        
        # 灵龟八法按钮
        ttk.Button(button_frame, text="灵龟八法开穴", command=self.calculate_acupoint).pack(side=tk.LEFT, padx=5)
        
        # 结果显示区域
        self.result_frame = ttk.LabelFrame(self.root, text="计算结果", padding=10)
        self.result_frame.pack(pady=10, padx=10, fill=tk.X)
        
        self.day_label = ttk.Label(self.result_frame, text="日干支：")
        self.day_label.pack(anchor=tk.W)
        
        self.hour_label = ttk.Label(self.result_frame, text="时干支：")
        self.hour_label.pack(anchor=tk.W)
        
        self.acupoint_label = ttk.Label(self.result_frame, text="灵龟八法：")
        self.acupoint_label.pack(anchor=tk.W)
        
        self.process_text = tk.Text(self.result_frame, height=8, width=40)
        self.process_text.pack(pady=5)
        
        # 存储计算结果
        self.day_sb = None
        self.hour_sb = None
        self.set_current_time()

    def set_current_time(self):
        now = datetime.now()
        self.cal.selection_set(now)
        self.hour_var.set(str(now.hour))
        self.minute_var.set(str(now.minute))
    
    def calculate_stem_branch(self):
        selected_date = self.cal.get_date()
        hour = int(self.hour_var.get())
        
        # 计算日干支
        self.day_sb = ChineseCalendar.get_day_stem_branch(selected_date)
        # 计算时干支
        self.hour_sb = ChineseCalendar.get_hour_stem_branch(self.day_sb[0], hour)
        
        # 更新显示
        self.day_label.config(text=f"日干支：{self.day_sb}")
        self.hour_label.config(text=f"时干支：{self.hour_sb}")
        
        # 清空之前的灵龟八法结果
        self.acupoint_label.config(text="灵龟八法：")
        self.process_text.delete(1.0, tk.END)
    
    def calculate_acupoint(self):
        if not self.day_sb or not self.hour_sb:
            self.calculate_stem_branch()
        
        # 计算灵龟八法
        result = LingGuiBaFa.calculate_acupoint(self.day_sb, self.hour_sb)
        
        # 更新显示
        self.acupoint_label.config(text=f"灵龟八法：应开穴位 - {result['穴位']}")
        
        # 显示计算过程
        process_text = (
            f"计算过程：\n"
            f"日干（{self.day_sb[0]}）：{result['日干数']}\n"
            f"日支（{self.day_sb[1]}）：{result['日支数']}\n"
            f"时干（{self.hour_sb[0]}）：{result['时干数']}\n"
            f"时支（{self.hour_sb[1]}）：{result['时支数']}\n"
            f"总和：{result['总和']}\n"
            f"{result['阴阳']}日，除以{result['除数']}，余数为{result['余数']}\n"
            f"对应穴位：{result['穴位']}"
        )
        self.process_text.delete(1.0, tk.END)
        self.process_text.insert(1.0, process_text)
    
    def run(self):
        self.root.mainloop()

def main():
    calculator = Calculator()
    calculator.run()

if __name__ == "__main__":
    main()
