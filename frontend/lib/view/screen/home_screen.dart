import 'package:flutter/material.dart';
import 'package:frontend/core/constant/app_colors.dart';
import 'package:frontend/data/model/user_model.dart';
import 'package:frontend/view/widget/cart.dart';
import 'package:frontend/view/widget/quick_acsess_card.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  UserModel? user;
  int _selectedIndex = 0;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    user = ModalRoute.of(context)?.settings.arguments as UserModel?;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FA),
      drawer: _buildDrawer(),
      bottomNavigationBar: _buildBottomNav(),
      body: SafeArea(
        child: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildHeader(),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: 20),
                    _buildSectionTitle("وثائقك الرقمية", onTap: () {}),
                    const SizedBox(height: 12),
                    _buildDigitalDocs(),
                    const SizedBox(height: 25),
                    _buildSectionTitle("الوصول السريع"),
                    const SizedBox(height: 12),
                    _buildQuickAccess(),
                    const SizedBox(height: 20),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // ===== HEADER =====
  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 18),
      decoration: BoxDecoration(color: AppColors.primary),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Builder(
            builder: (context) => GestureDetector(
              onTap: () => Scaffold.of(context).openDrawer(),
              child: Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.black.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: const Icon(Icons.menu, color: Colors.white, size: 24),
              ),
            ),
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                "مرحباً، ${user?.name ?? ''}",
                style: const TextStyle(
                  color: Colors.black,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Text(
                "أهلاً بك في منصة تشاد الرقمية",
                style: TextStyle(color: Colors.grey, fontSize: 13),
              ),
            ],
          ),
          CircleAvatar(
            radius: 24,
            backgroundColor: Colors.grey.withOpacity(0.3),
            child: const Icon(Icons.person, color: Colors.black, size: 28),
          ),
        ],
      ),
    );
  }

  // ===== SECTION TITLE =====
  Widget _buildSectionTitle(String title, {VoidCallback? onTap}) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        if (onTap != null)
          GestureDetector(
            onTap: onTap,
            child: const Text(
              "عرض الكل",
              style: TextStyle(
                color: Color.fromARGB(255, 0, 51, 139),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        Text(
          title,
          style: const TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Colors.black87,
          ),
        ),
      ],
    );
  }

  // ===== DIGITAL DOCS =====
  Widget _buildDigitalDocs() {
    return SizedBox(
      height: 120,
      child: ListView(
        scrollDirection: Axis.horizontal,
        physics: const BouncingScrollPhysics(),
        children: [
          CustomFeatureCard(
            title: "الهوية الوطنية",
            textColor: Colors.white,
            icon: Icons.badge_rounded,
            fontSize: 19,
            onTap: () {
              Navigator.of(context).pushNamed("NationalIdShowScreen");
            },
            startColor: const Color.fromARGB(255, 145, 123, 0),
            endColor: AppColors.buttonColor,
          ),
          const SizedBox(width: 15),
          CustomFeatureCard(
            title: "جواز السفر",
            fontSize: 19,
            textColor: Colors.white,
            icon: Icons.flight,
            onTap: () {
              Navigator.of(context).pushNamed("showpassport", arguments: user);
            },
            startColor: const Color.fromARGB(255, 0, 23, 108),
            endColor: Colors.blue,
          ),
          const SizedBox(width: 15),
          CustomFeatureCard(
            title: "رخصة القيادة",
            fontSize: 19,
            textColor: Colors.white,
            icon: Icons.directions_car,
            onTap: () {},
            startColor: const Color.fromARGB(255, 94, 9, 3),
            endColor: AppColors.error,
          ),
        ],
      ),
    );
  }

  // ===== QUICK ACCESS =====
  Widget _buildQuickAccess() {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisSpacing: 15,
      mainAxisSpacing: 15,
      children: [
        CustomQuickAcsessCard(
          title: "مواعيد الأحوال المدنية",
          icon: Icons.calendar_month_rounded,
          onTap: () {},
          startColor: AppColors.containerBackground,
          endColor: AppColors.containerBackground,
          textColor: Colors.black87,
        ),
        CustomQuickAcsessCard(
          title: "خدمات الهوية الوطنية",
          icon: Icons.badge_outlined,
          onTap: () {
            Navigator.of(
              context,
            ).pushNamed("NationalIdServices", arguments: user);
          },
          startColor: AppColors.containerBackground,
          endColor: AppColors.containerBackground,
          textColor: Colors.black87,
        ),
        CustomQuickAcsessCard(
          title: "خدمات شهادة الميلاد",
          icon: Icons.article_outlined,
          onTap: () {
            Navigator.of(
              context,
            ).pushNamed("birthCertificateServicesScreen", arguments: user);
          },
          startColor: AppColors.containerBackground,
          endColor: AppColors.containerBackground,
          textColor: Colors.black87,
        ),
        CustomQuickAcsessCard(
          title: "مواعيد الجوازات",
          icon: Icons.access_time_rounded,
          onTap: () {},
          startColor: AppColors.containerBackground,
          endColor: AppColors.containerBackground,
          textColor: Colors.black87,
        ),
        CustomQuickAcsessCard(
          title: "خدمات جواز السفر",
          icon: Icons.flight_outlined,
          onTap: () {
            Navigator.of(context).pushNamed("passportServices");
          },
          startColor: AppColors.containerBackground,
          endColor: AppColors.containerBackground,
          textColor: Colors.black87,
        ),
      ],
    );
  }

  // ===== DRAWER =====
  Widget _buildDrawer() {
    return Drawer(
      child: Column(
        children: [
          UserAccountsDrawerHeader(
            decoration: BoxDecoration(color: AppColors.background),
            accountName: Text(
              user?.name ?? "",
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 16,
                color: Colors.white,
              ),
            ),
            accountEmail: Text(user?.email ?? ""),
            currentAccountPicture: const CircleAvatar(
              backgroundColor: Colors.white,
              child: Icon(Icons.person, size: 40, color: Colors.grey),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.home_outlined),
            title: const Text("الرئيسية"),
            onTap: () => Navigator.pop(context),
          ),
          ListTile(
            leading: const Icon(Icons.person_outline),
            title: const Text("الملف الشخصي"),
            onTap: () {},
          ),
          ListTile(
            leading: const Icon(Icons.badge_outlined),
            title: const Text("وثائقي الرقمية"),
            onTap: () {},
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout, color: Colors.red),
            title: const Text(
              "تسجيل الخروج",
              style: TextStyle(color: Colors.red),
            ),
            onTap: () {
              Navigator.of(context).pushReplacementNamed("login");
            },
          ),
        ],
      ),
    );
  }

  // ===== BOTTOM NAV =====
  Widget _buildBottomNav() {
    return Container(
      height: 75,
      decoration: const BoxDecoration(color: AppColors.background),
      child: Column(
        children: [
          Container(
            height: 4,
            child: Row(
              children: [
                Expanded(
                  flex: 5,
                  child: Container(color: AppColors.background),
                ),
                Expanded(
                  flex: 5,
                  child: Container(color: AppColors.buttonColor),
                ),
                Expanded(flex: 5, child: Container(color: AppColors.error)),
              ],
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _buildNavIcon(Icons.home, 0, "الرئيسية"),
              _buildNavIcon(Icons.apps_outlined, 1, "خدمات أخرى"),
              _buildNavIcon(Icons.assignment_outlined, 2, "طلباتي"),
              _buildNavIcon(Icons.person_outline, 3, "حسابي"),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildNavIcon(IconData icon, int index, String label) {
    final isSelected = _selectedIndex == index;
    return InkWell(
      onTap: () {
        setState(() => _selectedIndex = index);
        switch (index) {
          case 0:
            Navigator.of(context).pushReplacementNamed("home");
            break;
          case 1:
            Navigator.of(context).pushReplacementNamed("OtherServices");
            break;
          case 2:
            Navigator.of(context).pushReplacementNamed("MyRequests");
            break;
          case 3:
            Navigator.of(context).pushReplacementNamed("MyAccount");
            break;
        }
      },
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 12),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              icon,
              color: isSelected
                  ? AppColors.containerBackground
                  : Colors.white70,
              size: 26,
            ),
            const SizedBox(height: 3),
            Text(
              label,
              style: TextStyle(
                color: isSelected
                    ? AppColors.containerBackground
                    : Colors.white70,
                fontSize: 10,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
