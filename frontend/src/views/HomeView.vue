<template>
  <div class="relative min-h-[100dvh] overflow-hidden bg-slate-950 text-white">
    <div
      aria-hidden="true"
      class="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_20%_20%,rgba(59,130,246,0.35),transparent_35%),radial-gradient(circle_at_80%_15%,rgba(168,85,247,0.32),transparent_30%),radial-gradient(circle_at_50%_90%,rgba(14,165,233,0.18),transparent_35%)]"
    />
    <div aria-hidden="true" class="absolute inset-0 bg-gradient-to-br from-slate-950 via-slate-900 to-indigo-950/80" />

    <div class="relative mx-auto flex min-h-full w-full max-w-6xl flex-col px-4 py-10 sm:px-6 lg:px-8">
      <header class="mb-10 text-center sm:mb-12">
        <p class="mb-3 inline-flex items-center rounded-full border border-white/15 bg-white/5 px-3 py-1 text-xs tracking-wide text-slate-300">
          AI Workspace
        </p>
        <h1 class="text-3xl font-semibold tracking-tight sm:text-4xl md:text-5xl">Superagent</h1>
        <p class="mx-auto mt-4 max-w-2xl text-sm leading-6 text-slate-300 sm:text-base">
          选择一个应用开始对话。界面采用流式交互，适配桌面端、平板与手机。
        </p>
      </header>

      <nav class="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6">
        <RouterLink
          class="group rounded-2xl border border-white/10 bg-white/10 p-6 shadow-[0_10px_40px_rgba(2,6,23,0.45)] backdrop-blur-xl transition duration-300 hover:scale-[1.02] hover:border-white/20 hover:shadow-[0_18px_50px_rgba(56,189,248,0.25)]"
          to="/gym"
        >
          <div class="mb-4 inline-flex h-11 w-11 items-center justify-center rounded-xl bg-cyan-400/20 text-xl">
            💪
          </div>
          <h2 class="text-xl font-semibold tracking-tight">AI Gym 大师</h2>
          <p class="mt-2 text-sm leading-6 text-slate-300">
            健身问答与连续会话，支持 SSE 实时流式回复，适合日常训练和饮食规划。
          </p>
          <div class="mt-5 inline-flex items-center text-sm text-cyan-300">
            进入应用
            <span class="ml-2 transition-transform group-hover:translate-x-1">→</span>
          </div>
        </RouterLink>

        <RouterLink
          class="group rounded-2xl border border-white/10 bg-white/10 p-6 shadow-[0_10px_40px_rgba(2,6,23,0.45)] backdrop-blur-xl transition duration-300 hover:scale-[1.02] hover:border-white/20 hover:shadow-[0_18px_50px_rgba(167,139,250,0.25)]"
          to="/manus"
        >
          <div class="mb-4 inline-flex h-11 w-11 items-center justify-center rounded-xl bg-violet-400/20 text-xl">
            🤖
          </div>
          <h2 class="text-xl font-semibold tracking-tight">AI 超级智能体</h2>
          <p class="mt-2 text-sm leading-6 text-slate-300">
            多步推理与工具调用。每次后端 step 完成后实时返回，适合复杂任务编排。
          </p>
          <div class="mt-5 inline-flex items-center text-sm text-violet-300">
            进入应用
            <span class="ml-2 transition-transform group-hover:translate-x-1">→</span>
          </div>
        </RouterLink>
      </nav>
    </div>
  </div>
</template>
